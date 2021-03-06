package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.nhs.digital.uec.api.exception.UnauthorisedException;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;

@SpringBootTest
@ActiveProfiles("local")
public class AuthenticationServiceTest {

  @Autowired private AuthenticationService authenticationService;

  @MockBean private CognitoIdpService cognitoIdpService;

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
    doReturn(authToken).when(cognitoIdpService).authenticate(credential);
    AuthToken returnedAuthToken = authenticationService.getAccessToken(credential);
    assertEquals(accessToken, returnedAuthToken.getAccessToken());
  }
}
