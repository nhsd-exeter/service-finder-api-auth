package uk.nhs.digital.uec.api.common.filter;

import uk.nhs.digital.uec.api.common.factory.CookieFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class that allows the cookies of a request to be edited when normally this is not possible.
 */
public class CookieRequestWrapper extends HttpServletRequestWrapper {

    private final CookieFactory cookieFactory;

    CookieRequestWrapper(HttpServletRequest request, CookieFactory cookieFactory) {
        super(request);
        this.cookieFactory = cookieFactory;
    }

    private String accessToken;

    private String refreshToken;

    @Override
    public Cookie[] getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        if (accessToken != null) {
            cookies.add(cookieFactory.createAccessToken(accessToken));
        }
        if (refreshToken != null) {
            cookies.add(cookieFactory.createRefreshToken(refreshToken));
        }

        Cookie[] cookiesArray = new Cookie[cookies.size()];
        return cookies.toArray(cookiesArray);
    }

    public void replaceTokenHeaders(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
