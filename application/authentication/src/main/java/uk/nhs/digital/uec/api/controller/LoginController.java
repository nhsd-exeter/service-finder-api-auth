package uk.nhs.digital.uec.api.controller;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.common.factory.CookieFactory;
import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.common.model.RefreshTokens;
import uk.nhs.digital.uec.api.adapter.UserAdapter;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.InvalidCredentialsException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NoRolesException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.model.UserLogin;
import uk.nhs.digital.uec.api.model.UserLoginResult;
import uk.nhs.digital.uec.api.service.PostcodeAPIService;
import uk.nhs.digital.uec.api.service.UserService;


/** Controller responsible for logging in a user */
@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
@Slf4j
public class LoginController {

  private final UserService userService;

  private final CookieFactory cookieFactory;

  private final UserAdapter userAdapter;

  private final PostcodeAPIService postcodeAPIService;

  @PostMapping
  public ResponseEntity<UserLoginResult> login(
      @RequestBody UserLogin userLogin, HttpServletResponse response)
      throws InvalidLoginException, NoRolesException, InvalidParameterException {
      long start = System.currentTimeMillis();
    String emailAddress = userLogin.getEmailAddress();
    User user =
        userAdapter
            .toUser(emailAddress)
            .orElseThrow(
                () ->
                    new InvalidCredentialsException(
                        String.format("Invalid credentials for [%s]", emailAddress)));
    Credentials credentials = userAdapter.toCredentials(userLogin, user);
    LoginResult resultPayload = userService.login(user, credentials);
    ResponseCookie accessTokenCookie = getMappedCookie(cookieFactory.createAccessToken(resultPayload.getAccessToken()));
    ResponseCookie refreshTokenCookie = getMappedCookie(cookieFactory.createRefreshToken(resultPayload.getRefreshToken()));
    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    UserLoginResult loginResult = userAdapter.toUserLoginResult(user);
    PostcodeMapping postcodeMapping =  postcodeAPIService.getRegionDetails(user.getPostcode());
    loginResult.setCcg(postcodeMapping.getCcg());
    log.info("Login completed {}ms", System.currentTimeMillis() - start);
    return ResponseEntity.ok(loginResult);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResult> refreshToken(@RequestBody RefreshTokens refreshTokens) {
    log.info("Refreshing user state {}",refreshTokens.getIdentityProviderId());
    LoginResult loginResult =
        userService.loginWithRefreshToken(
            refreshTokens.getRefreshToken(), refreshTokens.getIdentityProviderId());
    log.info("Refresh token renewed");
    return ResponseEntity.ok(loginResult);
  }

  private ResponseCookie getMappedCookie(Cookie cookie){
    return ResponseCookie.from(cookie.getName(),cookie.getValue())
      .domain(cookie.getDomain())
      .httpOnly(cookie.isHttpOnly())
      .maxAge(cookie.getMaxAge())
      .path(cookie.getPath())
      .secure(cookie.getSecure())
      .sameSite("None")
      .build();
  }


}
