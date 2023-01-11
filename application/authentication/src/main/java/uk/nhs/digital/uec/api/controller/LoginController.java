package uk.nhs.digital.uec.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationServiceInterface;

@RestController
public class LoginController {
  @Autowired
  private AuthenticationServiceInterface authenticationService;

  @PostMapping("/authentication/login")
  public ResponseEntity<AuthToken> getAccessToken(@RequestBody Credential credentials) throws UnauthorisedException {
    AuthToken resultPayload = authenticationService.getAccessToken(credentials);
    return ResponseEntity.ok(resultPayload);
  }

  @PostMapping("/authentication/refresh")
  public ResponseEntity<AuthToken> getAccessToken(@RequestHeader("REFRESH-TOKEN") String refreshToken,
      @RequestBody Credential credential) throws UnauthorisedException {
    AuthToken resultPayload = authenticationService.getAccessToken(refreshToken, credential.getEmailAddress());
    return ResponseEntity.ok(resultPayload);
  }

}
