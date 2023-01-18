package uk.nhs.digital.uec.api.localstub;

import com.amazonaws.services.cognitoidp.AbstractAWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;

import static uk.nhs.digital.uec.api.util.Constants.COGNITO_GROUP;
import static uk.nhs.digital.uec.api.util.Constants.PASSWORD;
import static uk.nhs.digital.uec.api.util.Constants.USERNAME;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.util.LocalJwtFactory;

@Slf4j
public class LocalAmazonCognitoIdentityClientStub extends AbstractAWSCognitoIdentityProvider {

  private final Map<String, String> identityProviderIdPasswordMap;

  public LocalAmazonCognitoIdentityClientStub() {
    identityProviderIdPasswordMap = new HashMap<>();
    identityProviderIdPasswordMap.put("admin@nhs.net", "password");
    identityProviderIdPasswordMap.put("service-finder-admin@nhs.net", "mock-auth-pass");
  }

  @Override
  public InitiateAuthResult initiateAuth(InitiateAuthRequest initiateAuthRequest) {
    Map<String, String> authParameters = initiateAuthRequest.getAuthParameters();
    String inputUserName = authParameters.get(USERNAME);
    String inputPassword = authParameters.get(PASSWORD);
    String validPassword = identityProviderIdPasswordMap.get(inputUserName);
    log.info("Login attempted using credentials : " + inputUserName + "/" + inputPassword);

    if (validPassword == null || !validPassword.equals(inputPassword)) {
      log.error("Attempted to login using invalid credentials");
      throw new AWSCognitoIdentityProviderException("401 - Unauthorised");
    } else {
      return initiateAuthRequest(inputUserName);
    }
  }

  private InitiateAuthResult initiateAuthRequest(String userName) {
    AuthenticationResultType authenticationResult = new AuthenticationResultType();
    authenticationResult.setAccessToken(generateAuthToken("id", "issuer", userName, 3600000));
    authenticationResult.setRefreshToken(generateAuthToken("rtid", "issuer", userName, 86400000));
    InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
    initiateAuthResult.setAuthenticationResult(authenticationResult);
    return initiateAuthResult;
  }

  private String generateAuthToken(String id, String issuer, String userName, long duration) {
    Set<String> cognitoGroupNames = new HashSet<>(List.of(COGNITO_GROUP));
    return new LocalJwtFactory().createToken(id, issuer, userName, duration, cognitoGroupNames);
  }
}
