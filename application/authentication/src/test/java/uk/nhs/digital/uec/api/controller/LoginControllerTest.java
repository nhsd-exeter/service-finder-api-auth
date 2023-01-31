package uk.nhs.digital.uec.api.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.nhs.digital.uec.api.common.UserManagementCommonConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.common.UserManagementCommonConstants.REFRESH_TOKEN;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.ACCESS_TOKEN_COOKIE;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.ACCESS_TOKEN_WITH_SEARCH_GROUP;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.REFRESH_TOKEN_COOKIE;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.REFRESH_TOKEN_VALUE;
import static uk.nhs.digital.uec.api.testsupport.AuthenticationResultTypeTestFactory.atestAuthenticationResultType;

import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import java.util.Collections;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.nhs.digital.uec.api.common.factory.CookieFactory;
import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.common.model.RefreshTokens;
import uk.nhs.digital.uec.api.adapter.UserAdapter;
import uk.nhs.digital.uec.api.model.Location;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.model.UserLogin;
import uk.nhs.digital.uec.api.model.UserLoginResult;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NoRolesException;
import uk.nhs.digital.uec.api.service.PostcodeAPIService;
import uk.nhs.digital.uec.api.service.UserService;

/** Test for {@link LoginController} */
@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();
  @Mock
  private UserService userService;
  @Mock
  private CookieFactory cookieFactory;
  @Mock
  private UserAdapter userAdapter;
  @Mock
  private PostcodeAPIService mockPostcodeAPIService;

  @InjectMocks
  private LoginController controller;

  @Test
  public void shouldLogin() throws InvalidLoginException, NoRolesException, InvalidParameterException {
    // Given
    String emailAddress = "test@example.com";
    UserLogin userLogin = new UserLogin();
    userLogin.setEmailAddress(emailAddress);
    userLogin.setPassword("password");

    InitiateAuthResult initiateAuthResult = new InitiateAuthResult();
    initiateAuthResult.setAuthenticationResult(atestAuthenticationResultType().build());

    LoginResult loginResult = new LoginResult(
        initiateAuthResult.getAuthenticationResult().getAccessToken(),
        initiateAuthResult.getAuthenticationResult().getRefreshToken());

    Credentials credentials = new Credentials();

    User user = new User();
    given(userAdapter.toUser(emailAddress)).willReturn(Optional.of(user));
    given(userAdapter.toCredentials(userLogin, user)).willReturn(credentials);
    given(userService.login(user, credentials)).willReturn(loginResult);

    SortedSet<Location> locationSortedSet = new TreeSet<>();
    locationSortedSet.add(new Location());
    UserLoginResult result = new UserLoginResult(
        emailAddress, "", Collections.emptySet(), "", "", "", "", "", "", locationSortedSet, null);
    given(userAdapter.toUserLoginResult(user)).willReturn(result);

    given(cookieFactory.createAccessToken(ACCESS_TOKEN_WITH_SEARCH_GROUP))
        .willReturn(new Cookie(ACCESS_TOKEN, ACCESS_TOKEN_WITH_SEARCH_GROUP));
    given(cookieFactory.createRefreshToken(REFRESH_TOKEN_VALUE))
        .willReturn(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN_VALUE));

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setCcg("dummyCCG");
    postcodeMapping.setName("dummyName");
    postcodeMapping.setRegion("dummyRegion");

    given(mockPostcodeAPIService.getRegionDetails(user.getPostcode())).willReturn(postcodeMapping);

    // When
    ResponseEntity loginResponse = controller.login(userLogin, response);

    // Then
    verify(userAdapter).toUserLoginResult(user);
    verify(response, times(1)).addHeader(HttpHeaders.SET_COOKIE, ACCESS_TOKEN_COOKIE);
    verify(response, times(1)).addHeader(HttpHeaders.SET_COOKIE, REFRESH_TOKEN_COOKIE);
    assertThat(loginResponse.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldFailGivenInvalidCredentialsException()
      throws InvalidLoginException, NoRolesException, InvalidParameterException {
    // Given
    String emailAddress = "test@example.com";
    UserLogin userLogin = new UserLogin();
    userLogin.setEmailAddress(emailAddress);
    userLogin.setPassword("password");

    given(userAdapter.toUser(emailAddress)).willThrow(InvalidCredentialsException.class);

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    // Expectations
    exceptionRule.expect(InvalidCredentialsException.class);

    // When
    controller.login(userLogin, response);
  }

  @Test
  public void shouldFailGivenInvalidLoginException()
      throws InvalidLoginException, NoRolesException, InvalidParameterException {
    // Given
    String emailAddress = "test@example.com";
    UserLogin userLogin = new UserLogin();
    userLogin.setEmailAddress(emailAddress);
    userLogin.setPassword("password");
    Credentials credentials = new Credentials();

    User user = new User();
    given(userAdapter.toUser(emailAddress)).willReturn(Optional.of(user));
    given(userAdapter.toCredentials(userLogin, user)).willReturn(credentials);
    given(userService.login(user, credentials)).willThrow(InvalidLoginException.class);

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    // Expectations
    exceptionRule.expect(InvalidLoginException.class);

    // When
    controller.login(userLogin, response);
  }

  @Test
  public void shouldFailGivenNoRolesException() throws InvalidLoginException, NoRolesException, InvalidParameterException {
    // Given
    String emailAddress = "test@example.com";
    UserLogin userLogin = new UserLogin();
    userLogin.setEmailAddress(emailAddress);
    userLogin.setPassword("password");
    Credentials credentials = new Credentials();

    User user = new User();
    given(userAdapter.toUser(emailAddress)).willReturn(Optional.of(user));
    given(userAdapter.toCredentials(userLogin, user)).willReturn(credentials);
    given(userService.login(user, credentials)).willThrow(NoRolesException.class);

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    // Expectations
    exceptionRule.expect(NoRolesException.class);

    // When
    controller.login(userLogin, response);
  }

@Test
  public void shouldReturnRefreshToken()  {
    RefreshTokens refreshTokens = new RefreshTokens(anyString(),anyString());
    LoginResult loginResult = new LoginResult();
    given(userService.loginWithRefreshToken(refreshTokens.getRefreshToken(), refreshTokens.getIdentityProviderId())).willReturn(loginResult);
    // When
    ResponseEntity loginResponse = controller.refreshToken(refreshTokens);
    assertThat(loginResponse.getStatusCode(), is(HttpStatus.OK));


  }
}
