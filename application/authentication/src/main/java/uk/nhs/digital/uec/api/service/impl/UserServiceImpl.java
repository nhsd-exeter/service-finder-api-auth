package uk.nhs.digital.uec.api.service.impl;

import static uk.nhs.digital.uec.api.common.UserManagementCommonConstants.COGNITO_GROUPS;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_ADMIN;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_SEARCH;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_APPROVED;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_REJECTED;


import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.adapter.PagedQueryAdapter;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.common.filter.JwtDecoder;
import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserChange;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.exception.AccountNotApprovedException;
import uk.nhs.digital.uec.api.exception.ApprovedAccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.ApprovedUserDeletionException;
import uk.nhs.digital.uec.api.exception.EmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.InvalidApprovalStatusChangeException;
import uk.nhs.digital.uec.api.exception.InvalidEntityCodeException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.exception.LoginAttemptsExceeded;
import uk.nhs.digital.uec.api.exception.LoginFailedInactiveUserException;
import uk.nhs.digital.uec.api.exception.NoRolesException;
import uk.nhs.digital.uec.api.exception.UserIncompleteException;
import uk.nhs.digital.uec.api.exception.UserMissingPersistentDataException;
import uk.nhs.digital.uec.api.exception.UserNotFoundException;
import uk.nhs.digital.uec.api.model.ForgotPasswordModel;
import uk.nhs.digital.uec.api.model.ResetPasswordModel;
import uk.nhs.digital.uec.api.repository.RoleRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;
import uk.nhs.digital.uec.api.service.AuditService;
import uk.nhs.digital.uec.api.service.CognitoService;
import uk.nhs.digital.uec.api.service.LoginAttemptService;
import uk.nhs.digital.uec.api.service.NotificationService;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.SynchronisationService;
import uk.nhs.digital.uec.api.service.UserChangeService;
import uk.nhs.digital.uec.api.service.UserService;
import uk.nhs.digital.uec.api.service.factory.UserQueryPageRequestFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/** Implementation of {@link UserService} */
@Transactional
@Service
@Slf4j
public class UserServiceImpl implements UserService {

  private static final String EMAIL_ADDRESS_ALREADY_REGISTERED =
      "That email address is already registered.";

  private static final String EMAIL_ADDRESS_PART_DELIMITER = "@";

  private static final String EMAIL_ADDRESS_WILDCARD = "*";

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final CognitoService cognitoService;

  private final SynchronisationService synchronisationService;

  private final NotificationService notificationService;

  private final LoginAttemptService loginAttemptService;

  private final AuditService auditService;

  private final UserChangeService userChangeService;

  private final Clock clock;

  private final UserQueryPageRequestFactory userQueryPageRequestFactory;

  private final JwtDecoder jwtDecoder;

  private final String adminEmailAddress;

  private final List<String> approvedDomains;

  @Autowired
  public UserServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      CognitoService cognitoService,
      SynchronisationService synchronisationService,
      NotificationService notificationService,
      LoginAttemptService loginAttemptService,
      AuditService auditService,
      UserChangeService userChangeService,
      Clock clock,
      UserQueryPageRequestFactory userQueryPageRequestFactory,
      JwtDecoder jwtDecoder,
      @Value("${servicefinder.admin.emailaddress}") String adminEmailAddress,
      @Value("${servicefinder.usermanagement.approvedDomains}") String[] approvedDomains) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.cognitoService = cognitoService;
    this.synchronisationService = synchronisationService;
    this.notificationService = notificationService;
    this.loginAttemptService = loginAttemptService;
    this.auditService = auditService;
    this.userChangeService = userChangeService;
    this.clock = clock;
    this.userQueryPageRequestFactory = userQueryPageRequestFactory;
    this.jwtDecoder = jwtDecoder;
    this.adminEmailAddress = adminEmailAddress;
    this.approvedDomains =
        approvedDomains == null ? Collections.emptyList() : List.of(approvedDomains);
  }

  /** {@inheritDoc} */
  @Override
  public LoginResult login(User user, Credentials credentials)
      throws NoRolesException, InvalidLoginException {
    CheckArgument.isNotNull(credentials, "credentials must not be null");
    String emailAddress = credentials.getEmailAddress();

    if (loginAttemptService.isBlocked(emailAddress)) {
      log.warn("{} attempts to login exceeded", emailAddress);
      throw new LoginAttemptsExceeded("Login attempts exceeded");
    }
    this.validateIncompleteUser(user);
    if (user.getUserState().equals(User.USER_STATE_INACTIVE)) {
      log.error("Inactive user attempted to login {}", user.getEmailAddress());
      throw new LoginFailedInactiveUserException();
    }
    if (!user.getApprovalStatus().equals("APPROVED")) {
      throw new AccountNotApprovedException();
    }
    try {
      log.info("Attempting to login user {}", credentials.getEmailAddress());
      LoginResult loginResult = cognitoService.authenticate(credentials);
      loginAttemptService.remove(emailAddress);
      checkAccessTokenGroups(loginResult);
      this.processLastLoggedInTime(user);
      log.info("Login successful returning user result {}", credentials.getEmailAddress());
      return loginResult;
    } catch (InvalidLoginException e) {
      log.error("Unable to login user {}", credentials.getEmailAddress(), e);
      loginAttemptService.add(emailAddress);
      throw new InvalidLoginException(String.format("Invalid credentials for [%s]", emailAddress));
    }
  }

  /** {@inheritDoc} */
  @Override
  public LoginResult loginWithRefreshToken(String refreshToken, String identityProviderId) {
    log.info("{} refreshing user state with refresh token", identityProviderId);
    CheckArgument.hasText(refreshToken, "refreshToken must not be blank");
    CheckArgument.hasText(identityProviderId, "identityProviderId must not be blank");
    return cognitoService.authenticateWithRefreshToken(refreshToken, identityProviderId);
  }

  /** {@inheritDoc} */
  @Override
  public User getById(long id) {
    return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
  }

  /** {@inheritDoc} */
  @Override
  public User getByIdentityProviderId(String identityProviderId) {
    CheckArgument.hasText(identityProviderId, "identityProviderId must not be blank");
    return userRepository
        .findByIdentityProviderId(identityProviderId)
        .orElseThrow(UserMissingPersistentDataException::new);
  }

  public Optional<User> getByEmailAddress(String emailAddress) {
    CheckArgument.hasText(emailAddress, "emailAddress must not be blank");
    return userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(emailAddress);
  }

  /** {@inheritDoc} */
  @Override
  public Page<User> findUsers(PagedQuery pagedQuery) {
    CheckArgument.isNotNull(pagedQuery, "query must not be null");
    PageRequest pageRequest = userQueryPageRequestFactory.createPageRequest(pagedQuery);
    return userRepository.findByQuery(pageRequest, pagedQuery.getFilterCriteria());
  }


  /** {@inheritDoc} */
  @Override
  public List<User> findUsers(Map<UserFilterCriteria, String> userFilterCriteria) {
    CheckArgument.isNotNull(userFilterCriteria, "filter criteria must not be null");
    return userRepository.findByQuery(userFilterCriteria);
  }

  /** {@inheritDoc} */
  @Override
  public User saveUser(User user) {
    CheckArgument.isNotNull(user, "user must not be null");
    try {
      return userRepository.saveAndFlush(user);
    } catch (DataIntegrityViolationException e) {
      throw handleDataIntegrityViolationException(e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public User update(User editedUser, User actor) {
    CheckArgument.isNotNull(editedUser, "editedUser must not be null");
    CheckArgument.isNotNull(actor, "actor must not be null");
    User user = userRepository.getOne(editedUser.getId());
    if (user.isEmailAddressVerified()) {
      String oldUserState = user.getUserState();
      userChangeService.recordUpdate(user, editedUser, actor);
      user.updateDetailsFromUser(editedUser, actor);
      processUserStateChange(user, editedUser.getUserState(), editedUser.getApprovalStatus());
      processApprovalStatusChange(
          user,
          editedUser.getApprovalStatus(),
          editedUser.getRejectionReason(),
          oldUserState,
          actor);
      processRoleChanges(user, editedUser.getRoles(), actor);
    }
    return user;
  }

  /** {@inheritDoc} */
  @Override
  public boolean resendConfirmationMessage(String emailAddress) {
    CheckArgument.isNotNull(emailAddress, "emailAddress must not be null");
    User user =
        userRepository
            .findFirstByEmailAddressIgnoreCaseOrderByIdAsc(emailAddress)
            .orElseThrow(
                () -> new EmailAddressNotRegisteredException(emailAddress + " is not registered"));
    if (user.getUserDetails() != null) {
      throw new ApprovedAccountAlreadyRegisteredException(emailAddress);
    }
    if ((user.isEmailAddressVerified()) && (!user.isApproved())) {
      notificationService.sendMessageForRegistrationPartII(user);
    } else {
      cognitoService.resendConfirmationMessage(user.getEmailAddress());
    }
    boolean isApproved = isApprovedDomain(emailAddress);
    return isApproved;
  }

  /** {@inheritDoc} */
  @Override
  public void forgotPassword(ForgotPasswordModel forgotPasswordModel, User user) {
    CheckArgument.isNotNull(user, "forgotPasswordModel must not be null");
    CheckArgument.isNotNull(user, "user must not be null");
    this.validateIncompleteUser(user);
    cognitoService.forgotPassword(forgotPasswordModel, user);
  }

  /** {@inheritDoc} */
  @Override
  public void resetPassword(ResetPasswordModel resetPasswordModel, User user) {
    CheckArgument.isNotNull(user, "resetPasswordModel must not be null");
    CheckArgument.isNotNull(user, "user must not be null");
    this.validateIncompleteUser(user);
    cognitoService.resetPassword(resetPasswordModel, user);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(User userForDeletion, User actor) {
    CheckArgument.isNotNull(userForDeletion, "userForDeletion must not be null");
    CheckArgument.isNotNull(actor, "actor must not be null");
    User user = userRepository.getOne(userForDeletion.getId());
    if (user.isEmailAddressVerified() && user.isApproved()) {
      throw new ApprovedUserDeletionException();
    }
    List<UserChange> userChanges = userChangeService.getAllRecordsByUser(userForDeletion.getId());
    if (userChanges != null && userChanges.size() > 0) {
      userChangeService.deleteAllRecordsByUser(userForDeletion.getId());
    }
    userRepository.delete(user);
    auditService.recordDeletion(actor, user);
    try {
      cognitoService.deleteUser(user);
    } catch (com.amazonaws.services.cognitoidp.model.UserNotFoundException e) {
      log.info(
          "User not found in cognito while attempting to delete user from service finder: ", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEmailInApprovedDomain(final String emailAddress) {
    return isApprovedDomain(emailAddress);
  }

  /** {@inheritDoc} */
  @Override
  public void updatePropertiesForApprovedUser(User user) {
    Role role =
        roleRepository
            .findByCode(ROLE_SEARCH)
            .orElseThrow(() -> new InvalidEntityCodeException("Could not find search role"));
    user.setRoles(new TreeSet<>(Collections.singletonList(role)));
    user.setApprovalStatus(APPROVAL_STATUS_APPROVED);
    synchronisationService.synchroniseRoles(user);
  }

  @Override
  public Page<UserAccount> findUnregisteredUsers(PagedQuery pagedQuery) {
    CheckArgument.isNotNull(pagedQuery, "query must not be null");
    PageRequest pageRequest = userQueryPageRequestFactory.createPageRequest(pagedQuery);
    return userRepository.findUnregisteredUserAccounts(pageRequest, pagedQuery.getFilterCriteria());
  }

  private void checkAccessTokenGroups(LoginResult loginResult) throws NoRolesException {
    DecodedJWT jwt = jwtDecoder.decode(loginResult.getAccessToken());
    Claim groupsClaim = jwt.getClaim(COGNITO_GROUPS);
    if (groupsClaim == null || groupsClaim.asList(String.class) == null) {
      log.info("NO GROUPS, NO ACCESS");
      throw new NoRolesException();
    }
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

  private void processApprovalStatusChange(
      User user,
      String newApprovalStatus,
      String newRejectionReason,
      String oldUserState,
      User actor) {
    String oldApprovalStatus = user.getApprovalStatus();
    if (!oldApprovalStatus.equals(newApprovalStatus)) {
      user.processApprovalStatusChange(
          newApprovalStatus, newRejectionReason, oldUserState, actor, clock);
      if (newApprovalStatus.equals(APPROVAL_STATUS_APPROVED)) {
        notificationService.sendApprovalMessage(user);
      } else if (newApprovalStatus.equals(APPROVAL_STATUS_REJECTED)) {
        notificationService.sendRejectionMessage(user);
      }
    }
  }

  private void processUserStateChange(User user, String newUserState, String newApprovalState) {
    String oldUserState = user.getUserState();
    if (!oldUserState.equals(newUserState)) {
      if (newUserState.equals(User.USER_STATE_ACTIVE)
          && newApprovalState.equals(User.APPROVAL_STATUS_REJECTED)) {
        throw new InvalidApprovalStatusChangeException(
            "Cannot change user state to INACTIVE while user is REJECTED");
      }
      user.setUserState(newUserState);
      if (newUserState.equals(User.USER_STATE_INACTIVE)) {
        user.setInactiveDate(LocalDateTime.now());
      } else {
        user.setInactiveDate(null);
      }
    }
  }

  private void processRoleChanges(User user, SortedSet<Role> newRoles, User actor) {
    if (actor.isAdmin()) {
      if (isAdminRoleCompulsory(user, actor)) {
        newRoles.add(
            roleRepository
                .findByCode(ROLE_ADMIN)
                .orElseThrow(
                    () -> new InvalidEntityCodeException("Invalid role code: " + ROLE_ADMIN)));
      }
    }
    if (user.getApprovalStatus().equals(APPROVAL_STATUS_APPROVED)) {
      newRoles.add(
          roleRepository
              .findByCode(ROLE_SEARCH)
              .orElseThrow(
                  () -> new InvalidEntityCodeException("Invalid role code: " + ROLE_SEARCH)));
    }
    user.setRoles(newRoles);
    synchronisationService.synchroniseRoles(user);
  }

  private void processLastLoggedInTime(User user) {
    user.setLastLoggedIn(LocalDateTime.now());
    userRepository.save(user);
  }

  private boolean isAdminRoleCompulsory(User user, User actor) {
    return ((user.isAdmin() && user.getEmailAddress().equals(actor.getEmailAddress()))
        || user.getEmailAddress().equals(adminEmailAddress));
  }

  private boolean isApprovedDomain(String emailAddress) {
    return approvedDomains.stream()
        .anyMatch(approvedDomain -> matchesApprovedDomain(emailAddress, approvedDomain));
  }

  private boolean matchesApprovedDomain(String emailAddress, String approvedDomain) {
    String emailEnding;
    if (approvedDomain.startsWith(EMAIL_ADDRESS_WILDCARD)) {
      emailEnding = approvedDomain.substring(1);
    } else {
      emailEnding = EMAIL_ADDRESS_PART_DELIMITER.concat(approvedDomain);
    }
    return emailAddress.toLowerCase().endsWith(emailEnding);
  }

  private InvalidRegistrationDetailsException createInvalidRegistrationDetailsException(
      String exceptionMessage) {
    return new InvalidRegistrationDetailsException(
        "There are validation errors", Collections.singletonList(exceptionMessage));
  }

  private void validateIncompleteUser(User user) {
    if (user.getUserDetails() == null) {
      throw new UserIncompleteException("User has not completed registration");
    }
    if (!user.isEmailAddressVerified()) {
      throw new UserIncompleteException("User has not completed registration");
    }
  }


}
