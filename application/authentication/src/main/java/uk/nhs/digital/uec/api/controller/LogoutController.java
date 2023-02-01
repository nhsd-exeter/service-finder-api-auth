package uk.nhs.digital.uec.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.common.factory.CookieFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller that logs a user out.
 */
@RestController
@RequestMapping("/api/logout")
@Slf4j
public class LogoutController {

    private final CookieFactory cookieFactory;

    @Autowired
    public LogoutController(CookieFactory cookieFactory) {
        this.cookieFactory = cookieFactory;
    }

    @PostMapping
    public ResponseEntity logout(HttpServletResponse response) {
      log.info("Invalidating user session");
        response.addCookie(cookieFactory.createAccessToken(null));
        response.addCookie(cookieFactory.createRefreshToken(null));
        return ResponseEntity.ok().build();
    }

}
