package uk.nhs.digital.uec.api.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;

import uk.nhs.digital.uec.api.config.CognitoUserPoolProperties;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.exception.UserManagementException;
import uk.nhs.digital.uec.api.service.factory.CognitoClientRequestSecretHashFactory;
import uk.nhs.digital.uec.api.service.impl.CognitoServiceDefaultImpl;
import uk.nhs.digital.uec.api.service.impl.EmailNotificationService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static uk.nhs.digital.uec.api.testsupport.UserRegistrationTestFactory.EMAIL_ADDRESS;
import static uk.nhs.digital.uec.api.testsupport.UserRegistrationTestFactory.PASSWORD;

/**
 * Test for {@link CognitoServiceDefaultImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterServiceDefaultImplTest {

    private static final String CLIENT_ID = "client_id";

    private static final String SECRET_HASH = "secret_hash";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private AWSCognitoIdentityProvider amazonCognitoIdentityClient;

    @Mock
    private CognitoUserPoolProperties cognitoUserPoolProperties;

    @Mock
    private CognitoClientRequestSecretHashFactory secretHashFactory;

    @Mock
    private EmailNotificationService emailNotificationService;

    private CognitoServiceDefaultImpl service;

    private Credentials credentials;

    @Before
    public void setUp() {
        given(cognitoUserPoolProperties.getClientId()).willReturn(CLIENT_ID);
        service = new CognitoServiceDefaultImpl(amazonCognitoIdentityClient, cognitoUserPoolProperties, secretHashFactory, emailNotificationService);
        credentials = new Credentials(EMAIL_ADDRESS, PASSWORD);
    }

    @Test
    public void shouldRegister() {
        // Given
        String subId = "0987654321";
        SignUpResult signUpResult = new SignUpResult().withUserSub(subId);
        given(amazonCognitoIdentityClient.signUp(any())).willReturn(signUpResult);
        given(secretHashFactory.create(EMAIL_ADDRESS)).willReturn(SECRET_HASH);

        // When
        service.register(credentials);

        // Then
        ArgumentCaptor<SignUpRequest> captor = ArgumentCaptor.forClass(SignUpRequest.class);
        verify(amazonCognitoIdentityClient).signUp(captor.capture());
        assertThat(captor.getValue().getClientId(), is(CLIENT_ID));
        assertThat(captor.getValue().getUsername(), is(EMAIL_ADDRESS));
        assertThat(captor.getValue().getPassword(), is(PASSWORD));
        assertThat(captor.getValue().getSecretHash(), is(SECRET_HASH));
    }

    @Test
    public void shouldFailToRegisterGivenNullCredentials() {
        // Given
        Credentials credentials = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("credentials must not be null");

        // When
        service.register(credentials);
    }

    @Test
    public void shouldThrowInvalidRegistrationDetailsExceptionGivenInvalidPasswordException() {
        // Given
        doThrow(new InvalidPasswordException("err")).when(amazonCognitoIdentityClient).signUp(any());

        // Expectations
        exceptionRule.expect(InvalidRegistrationDetailsException.class);

        // When
        service.register(credentials);
    }

    @Test
    public void shouldThrowInvalidRegistrationDetailsExceptionGivenUsernameExistsException() {
        //Given
        doThrow(new UsernameExistsException("err")).when(amazonCognitoIdentityClient).signUp(any());

        // Expectations
        exceptionRule.expect(InvalidRegistrationDetailsException.class);

        //When
        service.register(credentials);
    }

    @Test
    public void shouldThrowInvalidRegistrationDetailsExceptionGivenInvalidParameterException() {
        //Given
        doThrow(new InvalidParameterException("err")).when(amazonCognitoIdentityClient).signUp(any());

        // Expectations
        exceptionRule.expect(InvalidRegistrationDetailsException.class);

        //When
        service.register(credentials);
    }

    @Test
    public void shouldThrowUserManagementExceptionGivenAwsCognitoIdentityProviderException() {
        //Give
        Credentials credentials = new Credentials(EMAIL_ADDRESS, PASSWORD);
        doThrow(new AWSCognitoIdentityProviderException("err")).when(amazonCognitoIdentityClient).signUp(any());

        // Expectations
        exceptionRule.expect(UserManagementException.class);

        //When
        service.register(credentials);
    }

}
