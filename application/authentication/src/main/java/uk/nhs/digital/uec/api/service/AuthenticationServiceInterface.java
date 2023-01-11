package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;

public interface AuthenticationServiceInterface {
  AuthToken getAccessToken(Credential credentials) throws UnauthorisedException;
  AuthToken getAccessToken(String refreshToken, String email) throws UnauthorisedException;
}
