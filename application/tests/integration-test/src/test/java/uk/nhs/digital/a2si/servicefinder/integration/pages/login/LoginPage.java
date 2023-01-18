package uk.nhs.digital.a2si.servicefinder.integration.pages.login;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.a2si.servicefinder.integration.common.Driver;
import uk.nhs.digital.a2si.servicefinder.integration.helper.PageNavHelper;
import uk.nhs.digital.a2si.servicefinder.integration.pages.location.LocationPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.registration.RegistrationPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.resetpassword.ResetPasswordPage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LoginPage {

    private static Logger logger = LoggerFactory.getLogger(LoginPage.class);

    public static void visit() {
        String url = LoginPageElements.getPageUrl();
        LoginPageElements.goTo(url);
    }

    public static void login(String emailAddress, String password) {
        LoginPageElements.emailAddress().clear();
        LoginPageElements.emailAddress().sendKeys(emailAddress);
        LoginPageElements.password().sendKeys(password);
        LoginPageElements.loginButton().click();
    }

    public static void loginAsIncompleteUser() {
        login(RegistrationPage.TEST_EMAIL, "Password123");
    }

    public static void acceptCookiePolicy() {
        if (LoginPageElements.cookieConsentPresent()) {
        } else {
            LoginPageElements.cookieConsent().click();
        }
    }

    public static void logout() {
        if (logoutButtonDisplayed()) {
            LoginPageElements.logout().click();
        }
        assertThat(LoginPageElements.emailAddress().isDisplayed(), is(true));
    }

    public static void refreshPage() {
        if (LocationPage.onLocationPage()) {
            Driver.getWebDriver().navigate().refresh();
        }
    }

    private static boolean logoutButtonDisplayed() {
        try {
            return LoginPageElements.logout().isDisplayed();
        } catch (ElementNotFoundException e) {
            return false;
        }
    }

    public static void navigateToRegistrationPage() {
        LoginPageElements.registrationPageLink().click();
    }

    public static void waitUntilLoaded() {
        LoginPageElements.emailAddress();
    }

    public static void waitUntilCookiesDialogDisplayed() {
        LoginPageElements.cookieConsent();
    }

    public static void navigateToResetPasswordPage() {
        LoginPageElements.resetPasswordLink().click();
        ResetPasswordPage.waitUntilLoaded();
    }

    public static void navigateToResendVerificationLinkPage() {
        LoginPageElements.resendVerificationLink().click();
    }

    public static void navigateToHelpPage() {
        LoginPageElements.helpPageLink().click();
    }

    public static void mobNavigateToHelpPage() {
        expandMobMenu();
        LoginPageElements.helpPageLink().click();
    }

    public static void rememberEmailCheckBoxCanBeUnticked() {
        LoginPageElements.UntickEmailBox().click();
    }

    public static void rememberEmailCanBeReticked() {
        LoginPageElements.tickEmailBox().click();
        }

    public static void checkPasswordReveal() {
        LoginPageElements.password().clear();
        LoginPageElements.password().sendKeys("revealpassword");
        LoginPageElements.passwordRevealIcon().click();
    }

    public static void expandMobMenu() {
        LoginPageElements.mobMenu().click();
    }

    public static boolean isMobileBannerAvailable () {
        return LoginPageElements.mobileBanner().isDisplayed();
    }

    public static void selectCookiePolicyLinkFromCookieModal() {
      LoginPageElements.cookiePolicyLink().click();
    }

    public static void selectOptOutOfAnalyticsCookies() {
      LoginPageElements.optOutLink().click();
    }

    public static void selectNewURL() {
      LoginPageElements.clickNewURL().click();
    }

}
