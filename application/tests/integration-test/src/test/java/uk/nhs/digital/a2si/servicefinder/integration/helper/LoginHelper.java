package uk.nhs.digital.a2si.servicefinder.integration.helper;

import org.openqa.selenium.ElementNotInteractableException;

import uk.nhs.digital.a2si.servicefinder.integration.pages.login.LoginPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.login.LoginPageElements;
import uk.nhs.digital.a2si.servicefinder.integration.pages.registration.RegistrationPage;

public class LoginHelper {

    public static final String EMAIL_MISSING_ERROR = "Please enter an email address";

    private static final String email = RegistrationPage.TEST_EMAIL;

    public static String VERIFICATION_SENT = "We’ve sent a link to complete your account information to "+ email +". "+
                                            "You need to complete your account before you can start using the service.";

    public static final String INVALID_EMAIL = "Email address is not valid";

    public static void invalidLoginAttempt() {
        String username = "bob@nhs.net";
        String password = "invalid";
        LoginPage.visit();
        LoginPage.acceptCookiePolicy();
        LoginPage.login(username, password);
    }

    public static boolean loginAlertDisplayed() {
        try {
            return LoginPageElements.loginAlert().isDisplayed();
        } catch (ElementNotInteractableException e) {
            return false;
        }
    }



    public static boolean cookiePolicyIsDisplayed() {
        try {
            return LoginPageElements.cookieConsent().isDisplayed();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean loginFormDisplayed() {
        try {
            return LoginPageElements.emailAddress().isDisplayed() && LoginPageElements.password().isDisplayed();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean rememberEmailCheckBoxDisplayed() {
        try {
            return LoginPageElements.rememberEmailBox().isDisplayed();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean rememberEmailCheckBoxCanBeUnticked() {
        try {
            return LoginPageElements.UntickEmailBox().isDisplayed();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean rememberEmailCheckBoxCanBeTicked() {
        try {
            return LoginPageElements.tickEmailBox().isDisplayed();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isPasswordTextRevealed() {
        if (LoginPageElements.password().getAttribute("type").equals("text")==true){
            return true;
        } else{
            return false;
        }
    }
}
