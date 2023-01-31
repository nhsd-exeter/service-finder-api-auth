package uk.nhs.digital.uec.api.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;

import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.config.CognitoUserPoolProperties;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.UserManagementException;
import uk.nhs.digital.uec.api.service.factory.CognitoClientRequestSecretHashFactory;
import uk.nhs.digital.uec.api.service.impl.CognitoServiceDefaultImpl;
import uk.nhs.digital.uec.api.service.impl.EmailNotificationService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.REFRESH_TOKEN_VALUE;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.atestAuthenticationResultType;

/**
 * Test for {@link  CognitoServiceDefaultImpl}
 */
public class LoginServiceDefaultImplTest {

    private static final String USER_IDENTITY_PROVIDER_ID = "sub";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private AWSCognitoIdentityProvider amazonCognitoIdentityClient;

    private CognitoClientRequestSecretHashFactory factory;

    private CognitoUserPoolProperties cognitoUserPoolProperties;

    private CognitoServiceDefaultImpl service;

    private EmailNotificationService emailNotificationService;

    private String emailAddress = "test@example.com";

    private String password = "password";

    private Credentials credentials;



    @Before
    public void setUp() {
        credentials = new Credentials(emailAddress, password);
        amazonCognitoIdentityClient = mock(AWSCognitoIdentityProvider.class);
        factory = mock(CognitoClientRequestSecretHashFactory.class);
        cognitoUserPoolProperties = mock(CognitoUserPoolProperties.class);
        emailNotificationService = mock(EmailNotificationService.class);
        service = new CognitoServiceDefaultImpl(amazonCognitoIdentityClient, cognitoUserPoolProperties, factory, emailNotificationService);
    }

    @Test
    public void shouldAuthenticate() throws InvalidLoginException {
        // Given
        Map<String, String> authenticationParameters = new HashMap<>();
        authenticationParameters.put("USERNAME", emailAddress);
        authenticationParameters.put("PASSWORD", password);
        authenticationParameters.put("SECRET_HASH", factory.create(emailAddress));
        InitiateAuthRequest authenticationRequest = getAdminInitiateAuthRequest(AuthFlowType.USER_PASSWORD_AUTH, authenticationParameters);
        InitiateAuthResult authenticationResult = new InitiateAuthResult();
        authenticationResult.setAuthenticationResult(atestAuthenticationResultType().build());

        given(amazonCognitoIdentityClient.initiateAuth(authenticationRequest)).willReturn(authenticationResult);

        // When
        LoginResult loginResult = service.authenticate(credentials);

        // Then
        assertThat(loginResult.getAccessToken(), is(authenticationResult.getAuthenticationResult().getAccessToken()));
        assertThat(loginResult.getRefreshToken(), is(authenticationResult.getAuthenticationResult().getRefreshToken()));
    }

    @Test
    public void shouldAuthenticateWithRefreshToken() {
        // Given
        Map<String, String> authenticationParameters = new HashMap<>();
        authenticationParameters.put("REFRESH_TOKEN", REFRESH_TOKEN_VALUE);
        authenticationParameters.put("SECRET_HASH", factory.create(USER_IDENTITY_PROVIDER_ID));
        InitiateAuthRequest authenticationRequest = getAdminInitiateAuthRequest(AuthFlowType.REFRESH_TOKEN_AUTH, authenticationParameters);
        InitiateAuthResult authenticationResult = new InitiateAuthResult();
        authenticationResult.setAuthenticationResult(atestAuthenticationResultType().withRefreshToken(REFRESH_TOKEN_VALUE).build());

        given(amazonCognitoIdentityClient.initiateAuth(authenticationRequest)).willReturn(authenticationResult);

        // When
        LoginResult loginResult = service.authenticateWithRefreshToken(REFRESH_TOKEN_VALUE, USER_IDENTITY_PROVIDER_ID);

        // Then
        assertThat(loginResult.getAccessToken(), is(authenticationResult.getAuthenticationResult().getAccessToken()));
        assertThat(loginResult.getRefreshToken(), is(authenticationResult.getAuthenticationResult().getRefreshToken()));
    }

    @Test
    public void shouldFailToAuthenticateGivenNullUserLogin() throws InvalidLoginException {
        // Given
        Credentials credentials = null;

        try {
            // When
            service.authenticate(credentials);

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("credentials must not be null"));
        }
    }

    @Test
    public void shouldFailToAuthenticateGivenInvalidPasswordException() throws InvalidLoginException {
        // Given
        given(amazonCognitoIdentityClient.initiateAuth(any())).willThrow(InvalidPasswordException.class);

        // Expectations
        exceptionRule.expect(InvalidLoginException.class);

        //When
        service.authenticate(credentials);
    }

    @Test
    public void shouldFailToAuthenticateGivenNotAuthorizedException() throws InvalidLoginException {
        // Given
        given(amazonCognitoIdentityClient.initiateAuth(any())).willThrow(NotAuthorizedException.class);

        // Expectations
        exceptionRule.expect(InvalidLoginException.class);

        //When
        service.authenticate(credentials);
    }

    @Test
    public void authenticateShouldThrowUserManagementExceptionGivenAwsCognitoIdentityProviderException() throws InvalidLoginException {
        // Given
        doThrow(new UserManagementException("err")).when(amazonCognitoIdentityClient).initiateAuth(any());

        try {
            // When
            service.authenticate(credentials);

            // Then
            shouldHaveThrown(UserManagementException.class);
        } catch (UserManagementException e) {
            assertThat(e.getMessage(), is("err"));
        }
    }

    @Test
    public void authenticateShouldThrowInvalidCredentialsExceptionGivenUserNotFoundException() throws InvalidLoginException {
        // Given
        given(amazonCognitoIdentityClient.initiateAuth(any())).willThrow(UserNotFoundException.class);

        // Expectations
        exceptionRule.expect(InvalidCredentialsException.class);

        //When
        service.authenticate(credentials);
    }

    @Test
    public void shouldFailToAuthenticateWithRefreshTokenGivenNullRefreshToken() {
        // Given
        String refreshToken = null;

        try {
            // When
            service.authenticateWithRefreshToken(refreshToken, USER_IDENTITY_PROVIDER_ID);

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("refreshToken must have text"));
        }
    }

    @Test
    public void shouldFailToAuthenticateWithRefreshTokenGivenEmptyRefreshToken() {
        // Given
        String refreshToken = "";

        try {
            // When
            service.authenticateWithRefreshToken(refreshToken, USER_IDENTITY_PROVIDER_ID);

            // Then
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("refreshToken must have text"));
        }
    }

    @Test
    public void shouldFailToAuthenticateWithRefreshTokenGivenNullIdentityProviderId() {
        //Given
        String refreshToken = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("refreshToken must have text");

        //When
        service.authenticateWithRefreshToken(refreshToken, USER_IDENTITY_PROVIDER_ID);
    }

    @Test
    public void shouldFailToAuthenticateWithRefreshTokenGivenEmptyIdentityProviderId() {
        //Given
        String refreshToken = "";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("refreshToken must have text");

        //When
        service.authenticateWithRefreshToken(refreshToken, USER_IDENTITY_PROVIDER_ID);
    }

    private InitiateAuthRequest getAdminInitiateAuthRequest(AuthFlowType authFlowType, Map<String, String> authenticationParameters) {
        return new InitiateAuthRequest()
            .withAuthFlow(authFlowType)
            .withClientId(cognitoUserPoolProperties.getClientId())
            .withAuthParameters(authenticationParameters);
    }

}
