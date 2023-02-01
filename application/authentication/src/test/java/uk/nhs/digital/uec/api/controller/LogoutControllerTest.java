package uk.nhs.digital.uec.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.nhs.digital.uec.api.common.factory.CookieFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.nhs.digital.uec.api.common.UserManagementCommonConstants.ACCESS_TOKEN;
import static uk.nhs.digital.uec.api.common.UserManagementCommonConstants.REFRESH_TOKEN;

/**
 * Tests for {@link LogoutController}
 */
@RunWith(MockitoJUnitRunner.class)
public class LogoutControllerTest {

    private static final String COOKIE_DOMAIN = "localhost";

    private CookieFactory cookieFactory;

    private LogoutController controller;

    @Before
    public void setup() {
        cookieFactory = new CookieFactory(COOKIE_DOMAIN);
        controller = new LogoutController(cookieFactory);
    }

    // FIXME SM: response ends up with 4 cookies instead of 2 after logout
    @Test
    public void shouldLogout() {
        // Given
        HttpServletResponse response = mock(HttpServletResponse.class);
        String accessTokenValue = "TestAccessTokenValue";
        String refreshTokenValue = "TestRefreshTokenValue";
        response.addCookie(cookieFactory.createAccessToken(accessTokenValue));
        response.addCookie(cookieFactory.createRefreshToken(refreshTokenValue));
        List<Cookie> cookiesBeforeLogout = catchCookie(response, 2);
        assertThat(cookiesBeforeLogout.size(), is(2));

        // When
        ResponseEntity logoutResponse = controller.logout(response);

        // Then
        assertThat(logoutResponse.getStatusCode(), is(HttpStatus.OK));

        List<Cookie> cookies = catchCookie(response, 4);
        assertThat(cookies.size(), is(4));
        assertThat(cookies.get(0).getName(), is(ACCESS_TOKEN));
        assertThat(cookies.get(0).getValue(), is(accessTokenValue));
        assertThat(cookies.get(0).getMaxAge(), is(86400));
        assertThat(cookies.get(0).getDomain(), is(COOKIE_DOMAIN));
        assertThat(cookies.get(1).getName(), is(REFRESH_TOKEN));
        assertThat(cookies.get(1).getValue(), is(refreshTokenValue));
        assertThat(cookies.get(1).getMaxAge(), is(86400));
        assertThat(cookies.get(1).getDomain(), is(COOKIE_DOMAIN));

        assertThat(cookies.get(2).getName(), is(ACCESS_TOKEN));
        assertThat(cookies.get(2).getValue(), is(nullValue()));
        assertThat(cookies.get(2).getMaxAge(), is(0));
        assertThat(cookies.get(2).getDomain(), is(COOKIE_DOMAIN));
        assertThat(cookies.get(3).getName(), is(REFRESH_TOKEN));
        assertThat(cookies.get(3).getValue(), is(nullValue()));
        assertThat(cookies.get(3).getMaxAge(), is(0));
        assertThat(cookies.get(3).getDomain(), is(COOKIE_DOMAIN));
    }

    private List<Cookie> catchCookie(HttpServletResponse response, int invocationTimes) {
        ArgumentCaptor<Cookie> cookieArgumentCaptorCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(invocationTimes)).addCookie(cookieArgumentCaptorCaptor.capture());
        return cookieArgumentCaptorCaptor.getAllValues();
    }

}
