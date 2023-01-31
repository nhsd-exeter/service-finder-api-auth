package uk.nhs.digital.uec.api.config.test;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.cognitoidp.AbstractAWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminRemoveUserFromGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.CodeDeliveryDetailsType;
import com.amazonaws.services.cognitoidp.model.CodeMismatchException;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GroupType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.UserMissingPersistentDataException;
import uk.nhs.digital.uec.api.repository.UserRepository;

import org.apache.http.HttpStatus;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class TestAmazonCognitoIdentityClient extends AbstractAWSCognitoIdentityProvider {

    private static final String AUTH_PARAM_USERNAME = "USERNAME";

    private static final String AUTH_PARAM_PASSWORD = "PASSWORD";

    private static final String EXPIRED_CODE = "000000";

    private static final String INVALID_CODE = "654321";

    private static final String CONFIRMATION_CODE = "123456";

    private static final String COGNITO_USER_ALREADY_EXISTS_MESSAGE = "An account with the given email already exists";

    private static final String REJECTED = "Rejected";

    private static final String ACCEPTED = "Accepted";

    private static final String ACCOUNT_ALREADY_EXISTS_IN_COGNITO = "account_in_cognito@nhs.net";

    private static final String CONFIRMED_ACCOUNT_IN_COGNITO_NOT_PRESENT_IN_DB = "confirmed_account_in_cognito_not_in_db@nhs.net";

    private static final String CONFIRMED_UNAPPROVED_ACCOUNT_IN_COGNITO_PRESENT_IN_DB = "unapproved_confirmed@nonnhs.net";

    private static final String CONFIRMED_APPROVED_ACCOUNT_IN_COGNITO_PRESENT_IN_DB = "approved_confirmed@nhs.net";

    private static final String ACCOUNT_DOESNT_EXISTS_IN_COGNITO = "account_not_in_cognito@nhs.net";

    private final Map<String, String> identityProviderIdPasswordMap;

    private final UserRepository userRepository;

    public TestAmazonCognitoIdentityClient(UserRepository userRepository) {
        this.userRepository = userRepository;
        identityProviderIdPasswordMap = new HashMap<>();
        identityProviderIdPasswordMap.put("SUB_ID_UNCONFIRMED", "password");
        identityProviderIdPasswordMap.put("SUB_ID_SEARCH", "password");
        identityProviderIdPasswordMap.put("SUB_ID_REPORTER", "password");
        identityProviderIdPasswordMap.put("SUB_ID_APPROVER", "password");
        identityProviderIdPasswordMap.put("SUB_ID_ADMIN", "password");
        identityProviderIdPasswordMap.put("SUB_ID_SUPER", "password");
        identityProviderIdPasswordMap.put("UNAPPROVED_CONFIRMED", "password");
    }

    @Override
    public InitiateAuthResult initiateAuth(InitiateAuthRequest initiateAuthRequest) {
        Map<String, String> authParameters = initiateAuthRequest.getAuthParameters();
        String username = authParameters.get(AUTH_PARAM_USERNAME);
        String password = authParameters.get(AUTH_PARAM_PASSWORD);
        log.info("Login attempt from : " + username + "/" + password);

        User user = retrieveUser(username).orElseThrow(UserMissingPersistentDataException::new);
        String identityProviderId = user.getIdentityProviderId();

        if (!identityProviderIdPasswordMap.get(identityProviderId).equals(password)) {
            log.info(REJECTED);
            throw new UserMissingPersistentDataException();
        }
        log.info(ACCEPTED);

        Set<String> groupNames = user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());

        AuthenticationResultType authenticationResult = new AuthenticationResultType();
        TestJwtFactory testJwtFactory = new TestJwtFactory();
        String accessToken = testJwtFactory.create("id", "issuer", username, 3600000, groupNames, identityProviderId);
        authenticationResult.setAccessToken(accessToken);
        String refreshToken = testJwtFactory.create("rtid", "issuer", username, 86400000, new HashSet<>(), null);
        authenticationResult.setRefreshToken(refreshToken);
        InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
        initiateAuthResult.setAuthenticationResult(authenticationResult);
        return initiateAuthResult;
    }

    @Override
    public SignUpResult signUp(SignUpRequest signUpRequest) {
        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        log.info("Registration attempt from : " + username + "/" + password);

        String identityProviderId = UUID.nameUUIDFromBytes(username.getBytes(Charset.defaultCharset())).toString();

        if (ACCOUNT_ALREADY_EXISTS_IN_COGNITO.equals(username)) {
            log.info(REJECTED);
            throw new UsernameExistsException(COGNITO_USER_ALREADY_EXISTS_MESSAGE);
        }

        identityProviderIdPasswordMap.put(identityProviderId, password);
        log.info(ACCEPTED);

        SignUpResult signUpResult = new SignUpResult();
        signUpResult.setUserSub(identityProviderId);
        return signUpResult;
    }

    @Override
    public ConfirmSignUpResult confirmSignUp(ConfirmSignUpRequest confirmSignUpRequest) {
        String username = confirmSignUpRequest.getUsername();
        String confirmationCode = confirmSignUpRequest.getConfirmationCode();
        log.info("Verification attempt for : " + username + "/" + confirmationCode);

        if (INVALID_CODE.equals(confirmationCode)) {
            log.info(REJECTED);
            throw new CodeMismatchException("Invalid code");
        }

        if(EXPIRED_CODE.equals(confirmationCode)){
            throw new ExpiredCodeException("Code expired");
        }

        if(ACCOUNT_DOESNT_EXISTS_IN_COGNITO.equals(username)){
            throw new UserNotFoundException("Email address not registered.");
        }

        log.info(ACCEPTED);
        HttpResponse httpResponse = new HttpResponse(null, null);
        httpResponse.setStatusCode(HttpStatus.SC_OK);
        ConfirmSignUpResult confirmSignUpResult = new ConfirmSignUpResult();
        confirmSignUpResult.setSdkHttpMetadata(SdkHttpMetadata.from(httpResponse));
        return confirmSignUpResult;
    }

    @Override
    public ResendConfirmationCodeResult resendConfirmationCode(ResendConfirmationCodeRequest resendConfirmationCodeRequest) {
        String username = resendConfirmationCodeRequest.getUsername();
        log.info("Resend confirmation code attempt for : " + username);

        if (ACCOUNT_DOESNT_EXISTS_IN_COGNITO.equals(username))
        {
            throw new UserNotFoundException("Email address not registered.");
        }

        log.info(ACCEPTED);
        return new ResendConfirmationCodeResult();
    }

    @Override
    public ForgotPasswordResult forgotPassword(ForgotPasswordRequest request) {

        String username = request.getUsername();
        log.info("Forgot password attempt from : " + username);
        retrieveUser(username).orElseThrow(() -> new UserNotFoundException(username + " not found in db"));
        log.info(ACCEPTED);

        ForgotPasswordResult result = new ForgotPasswordResult();
        CodeDeliveryDetailsType codeDeliveryDetails = new CodeDeliveryDetailsType();
        codeDeliveryDetails.setDeliveryMedium(DeliveryMediumType.EMAIL);
        result.setCodeDeliveryDetails(codeDeliveryDetails);
        return result;
    }

    @Override
    public ConfirmForgotPasswordResult confirmForgotPassword(ConfirmForgotPasswordRequest request) {

        String username = request.getUsername();
        String code = request.getConfirmationCode();
        String password = request.getPassword();
        log.info("Confirm forgot password attempt from : " + username + "/" + code + "/" + password);

        retrieveUser(username).orElseThrow(() -> new UserNotFoundException(username + " not found in db"));

        if (EXPIRED_CODE.equals(code)) {
            log.info(REJECTED);
            throw new ExpiredCodeException("code expired");
        }

        if (!CONFIRMATION_CODE.equals(code)) {
            log.info(REJECTED);
            throw new CodeMismatchException("code mismatch");
        }
        if ("".equals(password)) {
            log.info(REJECTED);
            throw new InvalidPasswordException("bad password");
        }
        log.info(ACCEPTED);

        return new ConfirmForgotPasswordResult();
    }

    private Optional<User> retrieveUser(String username) {
        User user = userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(username).orElse(null);
        if (user == null || !identityProviderIdPasswordMap.containsKey(user.getIdentityProviderId())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public AdminAddUserToGroupResult adminAddUserToGroup(AdminAddUserToGroupRequest request) {
        return new AdminAddUserToGroupResult();
    }

    @Override
    public AdminRemoveUserFromGroupResult adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest request) {
        return new AdminRemoveUserFromGroupResult();
    }

    @Override
    public AdminListGroupsForUserResult adminListGroupsForUser(AdminListGroupsForUserRequest request) {
        String emailAddress = request.getUsername();
        User user = userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(emailAddress).get();
        AdminListGroupsForUserResult results = new AdminListGroupsForUserResult();
        Collection<GroupType> groupTypeCollection = user.getRoles().stream().map(r -> new GroupType().withGroupName(r.getName())).collect(Collectors.toList());
        results.setGroups(groupTypeCollection);
        return results;
    }

    @Override
    public AdminDeleteUserResult adminDeleteUser(AdminDeleteUserRequest request) {
        identityProviderIdPasswordMap.remove(request.getUsername());
        return new AdminDeleteUserResult();
    }

    @Override
    public AdminUpdateUserAttributesResult adminUpdateUserAttributes(AdminUpdateUserAttributesRequest request)
    {
        log.info("Adding user attributes...");
        String attribute = request.getUserAttributes().get(0).getName();

        log.info("Adding attribute: " + attribute + " to user: " + request.getUsername());
        log.info("ACCEPTED");

        return new AdminUpdateUserAttributesResult();
    }

    @Override
    public AdminGetUserResult adminGetUser(AdminGetUserRequest request)
    {
        log.info("Retrieving user attributes for: " + request.getUsername());

        if(ACCOUNT_DOESNT_EXISTS_IN_COGNITO.equals(request.getUsername())){
            throw new UserNotFoundException("Email address not registered.");
        }

        AttributeType attribute = new AttributeType()
            .withName("custom:UUID")
            .withValue("12345678");

        String userStatus = "UNCONFIRMED";
        if(CONFIRMED_ACCOUNT_IN_COGNITO_NOT_PRESENT_IN_DB.equals(request.getUsername()) ||
            CONFIRMED_UNAPPROVED_ACCOUNT_IN_COGNITO_PRESENT_IN_DB.equals(request.getUsername())||
            CONFIRMED_APPROVED_ACCOUNT_IN_COGNITO_PRESENT_IN_DB.equals(request.getUsername())){
            userStatus = "CONFIRMED";
        }

        AdminGetUserResult result = new AdminGetUserResult()
            .withUsername(request.getUsername())
            .withUserStatus(userStatus)
            .withUserAttributes(attribute);

        return result;
    }

}
