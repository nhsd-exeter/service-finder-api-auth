package uk.nhs.digital.a2si.servicefinder.integration.pages.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.nhs.digital.a2si.servicefinder.integration.pages.ParentPageElements;

public class LoginPageElements extends ParentPageElements {

  public static WebElement emailAddress() {
    return getElement(By.cssSelector("[data-locator='text-field-email-address']"));
  }

  public static WebElement password() {
    return getElement(By.cssSelector("[data-locator='text-field-password']"));
  }

  public static WebElement loginButton() {
    return getElement(By.cssSelector("[data-locator=button-login-continue]"));
  }

  public static WebElement loginButtonNoClick() {
    return getElementNoWait(By.cssSelector("[data-locator=button-login-continue]"));
  }

  public static WebElement logout() {
    return getElementByDataLocator("link-to-sign-out");
  }

  public static WebElement cookieConsent() {
    return getElement(By.cssSelector("[data-locator='button-agree-to-cookies']"));
  }

  public static WebElement confirmLogout() {
    return getElement(By.cssSelector("[data-locator='button-confirm-logout']"));
  }

  public static boolean cookieConsentPresent() {
    return getConsentCookieDecision() != null;
  }

  public static WebElement loginAlert() {
    return getElement(By.cssSelector("div[data-locator='help-block-locator']"));
  }

  public static WebElement registrationPageLink() {
    return getElement(By.cssSelector("[data-locator='link-to-register-page']"));
  }

  public static WebElement resetPasswordLink() {
    return getElementByDataLocator("link-to-forgot-password-page");
  }

  public static WebElement resendVerificationLink() {
    return getElementByDataLocator("link-to-resend-verification-link-page");
  }

  public static WebElement helpPageLink() {
    return getElementByDataLocator("link-to-help-page");
  }

  public static WebElement rememberEmailBox() {
    return getElementByDataLocator("checkbox-remember-email");
  }

  public static WebElement UntickEmailBox() {
    return getElementByDataLocator("checkbox-remember-email");
  }

  public static WebElement tickEmailBox() {
    return getElementByDataLocator("checkbox-remember-email");
  }

  public static WebElement passwordRevealIcon() {
    return getElementByDataLocator("password-reveal-toggle");
  }

  public static WebElement mobMenu() {
    return getElementByDataLocator("button-nav-open-menu");
  }

  public static WebElement mobileBanner() {
    return waitUntilPresent(By.cssSelector("[data-locator='add-to-home-screen-default-button']"));
  }

  public static WebElement cookiePolicyLink() {
    return getElementByDataLocator("modal-link-to-cookie-policy");
  }

  public static WebElement optOutLink() {
    return getElementByDataLocator("modal-link-to-GA-optout");
  }

  public static WebElement clickNewURL() {
    return getElementByLinkText("servicefinder.nhs.uk");
  }

}
