package uk.nhs.digital.uec.api.localstub;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.nhs.digital.uec.api.util.Utils.calculateSecretHash;
import static uk.nhs.digital.uec.api.util.Constants.USERNAME;
import static uk.nhs.digital.uec.api.util.Constants.PASSWORD;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;

public class LocalAmazonCognitoIdentityClientStubTest {
  private LocalAmazonCognitoIdentityClientStub amazonCognitoIdentityClientStub;

  @BeforeEach
  public void setup() {
    amazonCognitoIdentityClientStub = new LocalAmazonCognitoIdentityClientStub();
  }

  @Test
  public void initiateAuthRequestTest() {
    Map<String, String> authenticationParameters = Map.of(
        USERNAME,
        "admin@nhs.net",
        PASSWORD,
        "password",
        "SECRET_HASH",
        calculateSecretHash(
            "admin@nhs.net", "testUserPoolClientId", "testUserPoolClientSecret"));

    InitiateAuthRequest authenticationRequest = new InitiateAuthRequest()
        .withAuthFlow("authFlowType")
        .withClientId("testUserPoolClientId")
        .withAuthParameters(authenticationParameters);

    InitiateAuthResult initiateAuth = amazonCognitoIdentityClientStub.initiateAuth(authenticationRequest);
    assertNotNull(initiateAuth.getAuthenticationResult().getAccessToken());
    assertNotNull(initiateAuth.getAuthenticationResult().getRefreshToken());
  }

  @Test
  public void ShouldThrowAWSCognitoIdentityProviderExceptionForWrongPasswordTest() {
    Map<String, String> authenticationParameters = Map.of(
        USERNAME,
        "admin1@nhs.net",
        PASSWORD,
        "dummypassword",
        "SECRET_HASH",
        calculateSecretHash(
            "admin@nhs.net", "testUserPoolClientId", "testUserPoolClientSecret"));

    InitiateAuthRequest authenticationRequest = new InitiateAuthRequest()
        .withAuthFlow("authFlowType")
        .withClientId("testUserPoolClientId")
        .withAuthParameters(authenticationParameters);

    assertThrows(AWSCognitoIdentityProviderException.class,
        () -> amazonCognitoIdentityClientStub.initiateAuth(authenticationRequest));

  }

  @Test
  public void ShouldThrowAWSCognitoIdentityProviderExceptionForWrongPasswordTest2() {
    Map<String, String> authenticationParameters = Map.of(
        USERNAME,
        "admin@nhs.net",
        PASSWORD,
        "dummypassword",
        "SECRET_HASH",
        calculateSecretHash(
            "admin@nhs.net", "testUserPoolClientId", "testUserPoolClientSecret"));

    InitiateAuthRequest authenticationRequest = new InitiateAuthRequest()
        .withAuthFlow("authFlowType")
        .withClientId("testUserPoolClientId")
        .withAuthParameters(authenticationParameters);

    assertThrows(AWSCognitoIdentityProviderException.class,
        () -> amazonCognitoIdentityClientStub.initiateAuth(authenticationRequest));

  }

  @Test
  public void initiateMockAuthRequestTest() {
    Map<String, String> authenticationParameters = Map.of(
        USERNAME,
        "service-finder-admin@nhs.net",
        PASSWORD,
        "mock-auth-pass",
        "SECRET_HASH",
        calculateSecretHash(
            "service-finder-admin@nhs.net", "testUserPoolClientId", "testUserPoolClientSecret"));

    InitiateAuthRequest authenticationRequest = new InitiateAuthRequest()
        .withAuthFlow("authFlowType")
        .withClientId("testUserPoolClientId")
        .withAuthParameters(authenticationParameters);

    InitiateAuthResult initiateAuth = amazonCognitoIdentityClientStub.initiateAuth(authenticationRequest);
    assertNotNull(initiateAuth.getAuthenticationResult().getAccessToken());
    assertNotNull(initiateAuth.getAuthenticationResult().getRefreshToken());
  }
}
