package uk.nhs.digital.uec.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationServiceInterface;
import uk.nhs.digital.uec.api.service.CognitoIdpServiceInterface;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {

  @Autowired private CognitoIdpServiceInterface cognitoIdpService;

  @Override
  public AuthToken getAccessToken(Credential credentials) throws UnauthorisedException {
    return cognitoIdpService.authenticate(credentials);
  }

  @Override
  public AuthToken getAccessToken(String refreshToken, String email) throws UnauthorisedException {
    return cognitoIdpService.authenticate(refreshToken, email);
  }
}
