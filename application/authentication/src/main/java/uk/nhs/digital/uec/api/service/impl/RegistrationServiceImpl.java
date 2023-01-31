package uk.nhs.digital.uec.api.service.impl;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.adapter.UserRegistrationAdapter;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.exception.AccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.ApprovedAccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.exception.UserAccountMissingWhenCompletingRegistrationException;
import uk.nhs.digital.uec.api.model.ApprovalStatus;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.RegistrationRequestAccount;
import uk.nhs.digital.uec.api.model.RegistrationResult;
import uk.nhs.digital.uec.api.repository.UserAccountRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;
import uk.nhs.digital.uec.api.service.CognitoService;
import uk.nhs.digital.uec.api.service.NotificationService;
import uk.nhs.digital.uec.api.service.RegistrationService;
import uk.nhs.digital.uec.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "UserManagement - RegistrationService")
@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

  private static final String EMAIL_ADDRESS_ALREADY_REGISTERED =
      "Email address already registered.";

  @Autowired private CognitoService cognitoService;

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  @Autowired private UserAccountRepository userAccountRepository;

  @Autowired private UserRegistrationAdapter userRegistrationAdapter;

  @Autowired private NotificationService notificationService;

  /** {@inheritDoc} */
  @Override
  public void requestAccount(final RegistrationRequestAccount requestAccount) {
    log.info("Registering new user {}", requestAccount.getEmailAddress());
    CheckArgument.isNotNull(requestAccount, "requestAccount must not be null");

    if (userRepository
        .findFirstByEmailAddressIgnoreCaseOrderByIdAsc(requestAccount.getEmailAddress())
        .isPresent()) {
      log.error("{} already registered", requestAccount.getEmailAddress());
      throw createInvalidRegistrationDetailsException(EMAIL_ADDRESS_ALREADY_REGISTERED);
    }

    final Credentials credentials =
        new Credentials(requestAccount.getEmailAddress(), requestAccount.getPassword());

        try{
      // Attempt to create the user in Cognito.
      RegistrationResult registrationResult = cognitoService.register(credentials);
      log.info("{} successfully registred with cognito",requestAccount.getEmailAddress());
      UserAccount userAccount = new UserAccount();
      userAccount.setCreated(LocalDateTime.now());
      userAccount.setEmailAddress(requestAccount.getEmailAddress());
      userAccount.setEmailAddressVerified(false);
      userAccount.setIdentityProviderId(registrationResult.getIdentityProviderId());
      userAccount.setLastLoggedIn(null);
      userAccount.setUpdated(LocalDateTime.now());
      userAccount.setUpdatedBy(userAccount);
      userAccount.setUserDetails(null);
      userAccount.setUserState(UserAccount.USER_STATE_INACTIVE);
      userAccount.setInactiveDate(LocalDateTime.now());
      log.info("Saving new user to store");
      userAccountRepository.saveAndFlush(userAccount);
      log.info("{} successfully saved new user in DB",requestAccount.getEmailAddress());
    } catch (DataIntegrityViolationException e) {
      log.error("An error occurred registering user {}", e.getMessage());
      throw handleDataIntegrityViolationException(e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public ApprovalStatus verifyAccount(final EmailVerification emailVerification) {
    log.info("Verifying user account {}", emailVerification.getEmailAddress());
    // Get the user account out of the database. If it is missing throw an exception.
    Optional<User> accountInDb = userService.getByEmailAddress(emailVerification.getEmailAddress());
    if (!accountInDb.isPresent()) {
      log.error("An issue occurred verifying user account {}", emailVerification.getEmailAddress());
      throw new UserAccountMissingWhenCompletingRegistrationException(
          "User Account : "
              + emailVerification.getEmailAddress()
              + " is missing from database while trying to verify");
    }

    // First see if the user is already verified in Cognito
    AdminGetUserResult user =
        cognitoService.getUserByEmailAddress(emailVerification.getEmailAddress());

    boolean isEmailInApprovedDomain =
        userService.isEmailInApprovedDomain(emailVerification.getEmailAddress());
    ApprovalStatus approvalStatus = new ApprovalStatus(isEmailInApprovedDomain);

    String accountStatus = user.getUserStatus();
    if ("CONFIRMED".equals(accountStatus)) {

      // the account has been confirmed (verified), but has not completed the form
      // In the event where the account has been verified, but the form has not been completed
      // just return success with the approval status so that the UI can open the form.
      if (accountInDb.get().getUserDetails() != null) {
        // the user has already verified and completed the form

        if (approvalStatus.isApproved()) {
          log.error(
              "Found verified confirmed account {} but is already approved",
              accountInDb.get().getEmailAddress());
          throw new ApprovedAccountAlreadyRegisteredException(emailVerification.getEmailAddress());
        }
        log.error(
            "Found verified confirmed account {} but already registered",
            accountInDb.get().getEmailAddress());
        throw new AccountAlreadyRegisteredException(emailVerification.getEmailAddress());
      }
    } else {
      log.info("Verifying account via cognito {}", emailVerification.getEmailAddress());
      cognitoService.verify(emailVerification);
      UserAccount userAccount = accountInDb.get().getUserAccount();
      userAccount.setEmailAddressVerified(true);
      log.info("Setting verified status in data store {}", emailVerification.getEmailAddress());
      userAccountRepository.save(userAccount);
    }

    return approvalStatus;
  }

  /** {@inheritDoc} */
  @Override
  public void completeRegistration(final RegistrationCompleteAccount completeAccount) {
    CheckArgument.isNotNull(completeAccount, "completeAccount must not be null");
    log.info("Creating account for {}", completeAccount.getEmailAddress());
    // Construct initial user object based on the RegistrationCompleteAccount object.
    final User user = userRegistrationAdapter.toUser(completeAccount);
    // Update the approval status and user role if the email address is in the approved domain
    if (userService.isEmailInApprovedDomain(completeAccount.getEmailAddress())) {
      userService.updatePropertiesForApprovedUser(user);
      notificationService.sendApprovalMessage(user);
    }
    userRepository.saveAndFlush(user);
  }

  private InvalidRegistrationDetailsException createInvalidRegistrationDetailsException(
      String exceptionMessage) {
    return new InvalidRegistrationDetailsException(
        "There are validation errors", Collections.singletonList(exceptionMessage));
  }

  private RuntimeException handleDataIntegrityViolationException(
      DataIntegrityViolationException dataIntegrityViolationException) {
    String exceptionMessage = dataIntegrityViolationException.getMessage();
    if (exceptionMessage != null) {
      if (exceptionMessage.contains(
          "[user_email_address_key]; nested exception is"
              + " org.hibernate.exception.ConstraintViolationException")) {
        return createInvalidRegistrationDetailsException(EMAIL_ADDRESS_ALREADY_REGISTERED);
      }
    }
    return dataIntegrityViolationException;
  }
}
