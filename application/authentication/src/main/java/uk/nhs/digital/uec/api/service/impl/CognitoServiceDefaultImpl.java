package uk.nhs.digital.uec.api.service.impl;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.CodeMismatchException;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GroupType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.config.CognitoUserPoolProperties;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.AuthenticationException;
import uk.nhs.digital.uec.api.exception.CognitoNoUserAttributeException;
import uk.nhs.digital.uec.api.exception.EmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.InvalidCodeException;
import uk.nhs.digital.uec.api.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.exception.RegistrationEmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.RegistrationExpiredCodeException;
import uk.nhs.digital.uec.api.exception.RegistrationInvalidCodeException;
import uk.nhs.digital.uec.api.exception.UserManagementException;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.ForgotPasswordModel;
import uk.nhs.digital.uec.api.model.RegistrationResult;
import uk.nhs.digital.uec.api.model.ResetPasswordModel;
import uk.nhs.digital.uec.api.service.CognitoService;
import uk.nhs.digital.uec.api.service.factory.CognitoClientRequestSecretHashFactory;

/**
 * Implementation of {@link CognitoService} which uses the AWS Cognito identity client {@link AWSCognitoIdentityProvider}. Exceptions are mapped to custom ones.
 */
@Service
@Slf4j
public class CognitoServiceDefaultImpl implements CognitoService {

  private final AWSCognitoIdentityProvider identityClient;

  private final String userPoolClientId;

  private final String userPoolId;

  private final CognitoClientRequestSecretHashFactory secretHashFactory;

  private EmailNotificationService emailNotificationService;

  @Autowired
  public CognitoServiceDefaultImpl(
    AWSCognitoIdentityProvider identityClient,
    CognitoUserPoolProperties userPoolProperties,
    CognitoClientRequestSecretHashFactory secretHashFactory,
    EmailNotificationService emailNotificationService
  ) {
    this.identityClient = identityClient;
    this.userPoolId = userPoolProperties.getPoolId();
    this.userPoolClientId = userPoolProperties.getClientId();
    this.secretHashFactory = secretHashFactory;
    this.emailNotificationService = emailNotificationService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void verify(EmailVerification emailVerification) {
    CheckArgument.isNotNull(
      emailVerification,
      "emailVerification must not be null"
    );
    String emailAddress = emailVerification.getEmailAddress();
    String code = emailVerification.getCode();

    ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest()
      .withClientId(userPoolClientId)
      .withConfirmationCode(code)
      .withUsername(emailAddress)
      .withSecretHash(secretHashFactory.create(emailAddress));

    try {
      identityClient.confirmSignUp(confirmSignUpRequest);
    } catch (NotAuthorizedException e) {
      // TODO SM: following line exposes the email address in the logs
      log.error(
        "[{}] cannot be confirmed. Current status is CONFIRMED",
        emailAddress
      );
    } catch (CodeMismatchException e) {
      throw new RegistrationInvalidCodeException(emailAddress);
    } catch (UserNotFoundException e) {
      throw new RegistrationEmailAddressNotRegisteredException(emailAddress);
    } catch (com.amazonaws.services.cognitoidp.model.ExpiredCodeException e) {
      throw new RegistrationExpiredCodeException(emailAddress);
    } catch (AWSCognitoIdentityProviderException e) {
      log.error("Could not verify user", e);
      throw new UserManagementException("Could not verify user");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void resendConfirmationMessage(String emailAddress) {
    CheckArgument.isNotNull(emailAddress, "emailAddress must not be null");
    ResendConfirmationCodeRequest resendConfirmationCodeRequest = new ResendConfirmationCodeRequest()
      .withClientId(userPoolClientId)
      .withUsername(emailAddress)
      .withSecretHash(secretHashFactory.create(emailAddress));
    try {
      identityClient.resendConfirmationCode(resendConfirmationCodeRequest);
    } catch (UserNotFoundException e) {
      log.error("Username {} not found in Cognito user pool.", emailAddress);
      throw new EmailAddressNotRegisteredException(
        emailAddress + " not registered."
      );
    } catch (NotAuthorizedException e) {
      log.error(
        "Username {} not authorized. Exception:{}",
        emailAddress,
        e.getMessage()
      );
      throw new AuthenticationException(
        "Username not found in Cognito user pool."
      );
    } catch (AWSCognitoIdentityProviderException e) {
      log.error(
        "Username {} received AWSCognitoIdentityProviderException. Exception:{}",
        emailAddress,
        e.getMessage()
      );
      throw new UserManagementException("Could not resend confirmation code.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RegistrationResult register(Credentials credentials) {
    CheckArgument.isNotNull(credentials, "credentials must not be null");
    String emailAddress = credentials.getEmailAddress();
    SignUpRequest signUpRequest = new SignUpRequest()
      .withClientId(userPoolClientId)
      .withUsername(emailAddress)
      .withPassword(credentials.getPassword())
      .withSecretHash(secretHashFactory.create(emailAddress));
    SignUpResult signUpResult;
    try {
      signUpResult = identityClient.signUp(signUpRequest);
      return new RegistrationResult(signUpResult.getUserSub());
    } catch (
      InvalidPasswordException
      | UsernameExistsException
      | InvalidParameterException e
    ) {
      List<String> validationMessages =
        this.generateValidationMessages(e.getMessage());
      throw new InvalidRegistrationDetailsException(
        "There are validation errors",
        validationMessages
      );
    } catch (AWSCognitoIdentityProviderException e) {
      throw new UserManagementException(e.getMessage());
    }
  }

  private List<String> generateValidationMessages(String msg) {
    List<String> validationMessages = new ArrayList<>();
    // TODO SM: there is an issue in Cognito where despite the minimum password length set (in our case 8) the error message returned is always 6
    if (msg.contains("Member must have length greater than or equal to 6")) {
      validationMessages.add("Password must be longer than 6 chars");
    }
    if (msg.contains("Member must satisfy regular expression pattern")) {
      validationMessages.add("Password format invalid");
    }
    if (msg.contains("An account with the given email already exists")) {
      validationMessages.add("Email address already registered.");
    }
    if (msg.contains("Password did not conform with policy")) {
      validationMessages.add("The format of the password is invalid.");
    }
    if (validationMessages.isEmpty()) {
      log.warn("Unhandled message " + msg);
      validationMessages.add("There was a problem with the details supplied.");
    }
    return validationMessages;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LoginResult authenticate(Credentials credentials)
    throws InvalidLoginException {
    CheckArgument.isNotNull(credentials, "credentials must not be null");
    String emailAddress = credentials.getEmailAddress();
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put("USERNAME", emailAddress);
    authenticationParameters.put("PASSWORD", credentials.getPassword());
    authenticationParameters.put(
      "SECRET_HASH",
      secretHashFactory.create(emailAddress)
    );
    try {
      return getAuthenticationResult(
        AuthFlowType.USER_PASSWORD_AUTH,
        authenticationParameters
      );
    } catch (InvalidPasswordException | NotAuthorizedException e) {
      log.error("Unable to login user {}", credentials.getEmailAddress(), e);
      throw new InvalidLoginException(e.getMessage());
    } catch (UserNotFoundException e) {
      log.error("Unable to login user {}", credentials.getEmailAddress(), e);
      throw new InvalidCredentialsException(e.getMessage());
    } catch (AWSCognitoIdentityProviderException e) {
      log.error("Unable to login user {}", credentials.getEmailAddress(), e);
      throw new UserManagementException(e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LoginResult authenticateWithRefreshToken(
    String refreshToken,
    String identityProviderId
  ) {
    CheckArgument.hasText(refreshToken, "refreshToken must have text");
    CheckArgument.hasText(
      identityProviderId,
      "identityProviderId must have text"
    );
    Map<String, String> authenticationParameters = new HashMap<>();
    authenticationParameters.put("REFRESH_TOKEN", refreshToken);
    authenticationParameters.put(
      "SECRET_HASH",
      secretHashFactory.create(identityProviderId)
    );
    try {
      log.info("Attempting refresh login with user: {}", identityProviderId);
      LoginResult loginResult = getAuthenticationResult(
        AuthFlowType.REFRESH_TOKEN_AUTH,
        authenticationParameters
      );
      loginResult.setRefreshToken(refreshToken);
      return loginResult;
    } catch (AWSCognitoIdentityProviderException e) {
      log.error(
        "Unable to refresh user session using refresh token {}",
        identityProviderId
      );
      throw new UserManagementException(e.getMessage());
    }
  }

  private LoginResult getAuthenticationResult(
    AuthFlowType authFlowType,
    Map<String, String> authenticationParameters
  ) {
    InitiateAuthRequest authenticationRequest = new InitiateAuthRequest()
      .withAuthFlow(authFlowType)
      .withClientId(userPoolClientId)
      .withAuthParameters(authenticationParameters);
    InitiateAuthResult authenticationResult = identityClient.initiateAuth(
      authenticationRequest
    );
    return new LoginResult(
      authenticationResult.getAuthenticationResult().getAccessToken(),
      authenticationResult.getAuthenticationResult().getRefreshToken()
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void forgotPassword(
    ForgotPasswordModel forgotPasswordModel,
    User user
  ) {
    CheckArgument.isNotNull(
      forgotPasswordModel,
      "forgotPasswordModel must not be null"
    );
    String emailAddress = user.getEmailAddress();
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest()
      .withClientId(userPoolClientId)
      .withUsername(emailAddress)
      .withSecretHash(secretHashFactory.create(emailAddress));
    try {
      identityClient.forgotPassword(forgotPasswordRequest);
    } catch (UserNotFoundException e) {
      log.error("Username {} not found in Cognito user pool.", emailAddress);
    } catch (InvalidParameterException e) {
      log.error(
        "Username {} not yet verified in Cognito user pool.",
        emailAddress
      );
    } catch (AWSCognitoIdentityProviderException e) {
      log.error("Failed to execute forgotPassword", e);
      throw new UserManagementException("Failed to execute forgotPassword");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void resetPassword(ResetPasswordModel resetPasswordModel, User user) {
    CheckArgument.isNotNull(
      resetPasswordModel,
      "resetPasswordModel must not be null"
    );
    String emailAddress = user.getEmailAddress();
    ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest()
      .withClientId(userPoolClientId)
      .withConfirmationCode(resetPasswordModel.getCode())
      .withUsername(emailAddress)
      .withPassword(resetPasswordModel.getPassword())
      .withSecretHash(secretHashFactory.create(emailAddress));
    try {
      identityClient.confirmForgotPassword(confirmForgotPasswordRequest);
      emailNotificationService.sendSuccessfulPasswordResetMessage(
        user,
        LocalDateTime.now()
      );
    } catch (UserNotFoundException e) {
      log.error("Username {} not found in Cognito user pool.", emailAddress);
      throw new ExpiredCodeException(e.getMessage());
    } catch (CodeMismatchException e) {
      throw new InvalidCodeException(e.getMessage());
    } catch (com.amazonaws.services.cognitoidp.model.ExpiredCodeException e) {
      throw new ExpiredCodeException(e.getMessage());
    } catch (AWSCognitoIdentityProviderException e) {
      throw new UserManagementException(
        "Failed to execute confirmForgotPassword"
      );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> retrieveRoles(String emailAddress) {
    CheckArgument.hasText(emailAddress, "emailAddress must not be blank");
    AdminListGroupsForUserRequest request = new AdminListGroupsForUserRequest()
      .withUserPoolId(userPoolId)
      .withUsername(emailAddress);

    AdminListGroupsForUserResult result = identityClient.adminListGroupsForUser(
      request
    );
    return result
      .getGroups()
      .stream()
      .map(GroupType::getGroupName)
      .collect(Collectors.toSet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addRole(String roleCode, String emailAddress) {
    CheckArgument.hasText(roleCode, "roleCode must not be blank");
    CheckArgument.hasText(emailAddress, "emailAddress must not be blank");
    AdminAddUserToGroupRequest request = new AdminAddUserToGroupRequest()
      .withUserPoolId(userPoolId)
      .withUsername(emailAddress)
      .withGroupName(roleCode);
    identityClient.adminAddUserToGroup(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteRole(String roleCode, String emailAddress) {
    CheckArgument.hasText(roleCode, "roleCode must not be blank");
    CheckArgument.hasText(emailAddress, "emailAddress must not be blank");
    AdminRemoveUserFromGroupRequest request = new AdminRemoveUserFromGroupRequest()
      .withUserPoolId(userPoolId)
      .withUsername(emailAddress)
      .withGroupName(roleCode);
    identityClient.adminRemoveUserFromGroup(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUser(User user) {
    CheckArgument.isNotNull(user, "user must not be null");
    AdminDeleteUserRequest request = new AdminDeleteUserRequest()
      .withUserPoolId(userPoolId)
      .withUsername(user.getEmailAddress());
    identityClient.adminDeleteUser(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AdminGetUserResult getUserByEmailAddress(String emailAddress) {
    CheckArgument.isNotNull(emailAddress, "emailAddress must not be null");
    log.info("JMP - retrieving user: " + emailAddress);
    AdminGetUserRequest request = new AdminGetUserRequest()
      .withUserPoolId(userPoolId)
      .withUsername(emailAddress);

    try {
      return identityClient.adminGetUser(request);
    } catch (UserNotFoundException e) {
      log.info("JMP - user not found");
      throw new RegistrationEmailAddressNotRegisteredException(emailAddress);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String retrieveUserAttribute(
    final String emailAddress,
    final String attributeName
  ) {
    CheckArgument.isNotNull(emailAddress, "emailAddress must not be null");
    CheckArgument.isNotNull(attributeName, "attributeName must not be null");

    final AdminGetUserRequest request = new AdminGetUserRequest()
      .withUserPoolId(userPoolId)
      .withUsername(emailAddress);

    final AdminGetUserResult result = identityClient.adminGetUser(request);

    final List<AttributeType> userAttributes = result.getUserAttributes();

    for (AttributeType attribute : userAttributes) {
      if (attribute.getName().equals(attributeName)) {
        return attribute.getValue();
      }
    }

    throw new CognitoNoUserAttributeException(
      "Attribute " + attributeName + " not found."
    );
  }
}
