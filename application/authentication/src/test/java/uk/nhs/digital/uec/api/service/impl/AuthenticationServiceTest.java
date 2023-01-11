package uk.nhs.digital.uec.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.AuthenticationServiceInterface;
import uk.nhs.digital.uec.api.service.CognitoIdpServiceInterface;

@SpringBootTest
@ActiveProfiles("local")
public class AuthenticationServiceTest {

  @Autowired
  private AuthenticationServiceInterface authenticationService;

  @MockBean
  private CognitoIdpServiceInterface cognitoIdpService;

  private AuthToken authToken;
  private Credential credential;
  private final String accessToken = "123452AcEss-ToKen-Sample";

  @BeforeEach
  private void initialize() {
    credential = new Credential();
    credential.setEmailAddress("mock-user@xyz.com");
    credential.setPassword("pAssWord");
    authToken = AuthToken.builder()
        .accessToken(accessToken)
        .build();
  }

  @Test
  public void testAuthenticationService() throws UnauthorisedException {
    when(cognitoIdpService.authenticate(credential)).thenReturn(authToken);
    AuthToken returnedAuthToken = authenticationService.getAccessToken(credential);
    assertEquals(accessToken, returnedAuthToken.getAccessToken());
  }

  @Test
  public void getAccessTokenTestFromRefresh() throws UnauthorisedException {
    AuthToken authToken = new AuthToken("ACCESS_TOKEN_123", "REFRESH_TOKEN_123");
    String refreshToken = "REFRESH_TOKEN_123";
    when(cognitoIdpService.authenticate(refreshToken, credential.getEmailAddress()))
        .thenReturn(authToken);
    AuthToken accessToken = authenticationService.getAccessToken(refreshToken, credential.getEmailAddress());
    assertNotNull(accessToken.getAccessToken());
  }
}
