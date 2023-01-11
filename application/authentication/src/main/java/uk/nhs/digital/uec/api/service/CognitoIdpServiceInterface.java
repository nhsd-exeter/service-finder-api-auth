package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;

public interface CognitoIdpServiceInterface {

  public static final String SECRET_HASH = "SECRET_HASH";

  /**
   * This method implementation authenticate the user credentials connecting Cognito
   *
   * @param credential
   * @return AuthToken object
   */
  AuthToken authenticate(Credential credential) throws UnauthorisedException;
  AuthToken authenticate(String refreshToken, String email) throws UnauthorisedException;
}
