package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.impl.AuthenticationService;

@ExtendWith(SpringExtension.class)
public class LoginControllerTest {

@InjectMocks LoginController loginController;
@Mock AuthenticationService authenticationService;


  String refreshToken = "REFRESH_TOKEN_123";
  AuthToken authToken;
  String email = "admin@nhs.net";
  String password = "password";

  String wrongEmail = "wronguser@nhs.net";
  String wrongPassword = "wrongpassword";

  Credential cred;
  Credential wrongCred;




  @BeforeEach
  public void setup() {
    authToken = new AuthToken("ACCESS_TOKEN_123","REFRESH_TOKEN_123");
    cred = new Credential(email, password);
    wrongCred = new Credential(wrongEmail, wrongPassword);
  }




  @Test
  public void authorisedLoginTest() throws Exception {

    when(authenticationService.getAccessToken(cred)).thenReturn(authToken);
    var response = loginController.getAccessToken(cred);
    AuthToken authTokenResponse = (AuthToken) response.getBody();
    assertNotNull(authTokenResponse.getAccessToken());
    assertNotNull(authTokenResponse.getRefreshToken());
  }

  @Test
  public void unAuthorisedLoginTest() throws Exception {
    when(authenticationService.getAccessToken(wrongCred)).thenThrow(UnauthorisedException.class);
    assertThrows(UnauthorisedException.class, () -> loginController.getAccessToken(wrongCred));
  }


  @Test
  public void loginRefreshTest() throws NotFoundException, UnauthorisedException {
    when(authenticationService.getAccessToken(refreshToken, cred.getEmailAddress()))
        .thenReturn(authToken);
    var response = loginController.getAccessToken(refreshToken, cred);
    AuthToken authTokenResponse = (AuthToken) response.getBody();
    assertNotNull(authTokenResponse.getAccessToken());
  }

  @Test
  public void loginExceptionTest() throws NotFoundException, UnauthorisedException {
    when(authenticationService.getAccessToken(wrongCred)).thenThrow(UnauthorisedException.class);
    assertThrows(UnauthorisedException.class, () -> loginController.getAccessToken(wrongCred));

  }

  @Test
  public void loginRefreshExceptionTest() throws NotFoundException, UnauthorisedException {
    when(authenticationService.getAccessToken(refreshToken, wrongCred.getEmailAddress()))
        .thenThrow(UnauthorisedException.class);
    assertThrows(UnauthorisedException.class, () -> loginController.getAccessToken(refreshToken, wrongCred));
  }





}
