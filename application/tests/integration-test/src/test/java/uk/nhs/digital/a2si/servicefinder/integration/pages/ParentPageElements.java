package uk.nhs.digital.a2si.servicefinder.integration.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.a2si.servicefinder.integration.common.Driver;
import org.openqa.selenium.NoSuchElementException;

public class ParentPageElements {

    private static Logger logger = LoggerFactory.getLogger(ParentPageElements.class);

    private static final int TIME_OUT_IN_SECONDS = 10;

    private static final String ENV = System.getProperty("ENV");

    public static final String REGISTRATION_PART2_EMAIL = "regpart2"+ System.currentTimeMillis() + "@nhs.net";

    public static String registrationPart2Url = getPageUrl()+"/register/verified/true/"+ REGISTRATION_PART2_EMAIL +"/SUB_ID_UNCONFIRMED";

    public static final String SERVICE_UNAVAILABLE_URL = getPageUrl().substring(0, 28) + "/service-unavailable";

    public static final String UNREACHABLE_PAGE = getPageUrl().substring(0, 28) + "/s";

    public static void goTo(String url) {
        Driver.getWebDriver().get(url);
    }

    public static String getPageUrl() {
        Properties prop = Driver.getProperty();
        // The URL to use if the environment is "test";
        String url = prop.getProperty("testUrl");

        if (ENV.equals("demo")) {
            url = prop.getProperty("demoUrl");
        } else if (ENV.equals("prod")) {
            url = prop.getProperty("prodUrl");
        } else if (ENV.equals("nonprod")) {
            url = prop.getProperty("nonprodUrl");
        }
        return url;
    }

    protected static WebElement getElement(By selector) {
        return new WebDriverWait(Driver.getWebDriver(), TIME_OUT_IN_SECONDS).until(ExpectedConditions.elementToBeClickable(selector));
    }

    protected static WebElement getElementEvenIfHidden(By selector) {
        return new WebDriverWait(Driver.getWebDriver(), TIME_OUT_IN_SECONDS).until(ExpectedConditions.presenceOfElementLocated(selector));
    }

    protected static WebElement waitUntilPresent2(By selector) {
      return new WebDriverWait(Driver.getWebDriver(), 70).until(ExpectedConditions.visibilityOfElementLocated(selector));
  }

  protected static WebElement waitUntilPresent3(By selector) {
    return new WebDriverWait(Driver.getWebDriver(), 125).until(ExpectedConditions.visibilityOfElementLocated(selector));
}

    protected static WebElement waitUntilPresent(By selector) {
        return new WebDriverWait(Driver.getWebDriver(), 10).until(ExpectedConditions.visibilityOfElementLocated(selector));
    }

    protected static WebElement getElementNoWait(By selector) {
      try {
        return Driver.getWebDriver().findElement(selector);
      } catch (NoSuchElementException e) {
        return null;
      }
    }

    protected static boolean isElementPresentNoWait(By selector) {
        return Driver.getWebDriver().findElements(selector).size() > 0;
    }

    protected static boolean isElementPresentByDataLocatorNoWait(String dataLocator) {
        return isElementPresentNoWait(By.cssSelector("[data-locator='" + dataLocator + "']"));
    }

    protected static Cookie getConsentCookieDecision() {
        return Driver.getWebDriver().manage().getCookieNamed("user-has-accepted-cookies");
    }

    public static WebElement getPageBody() {
        return getElement(By.tagName("body"));
    }

    public static WebElement getElementByDataLocator(String dataLocator) {
        return getElement(By.cssSelector("[data-locator='" + dataLocator + "']"));
    }

    public static WebElement getElementByLinkText(String text){
      return getElement(By.className("nhsuk-body")).findElement(By.linkText(text));
    }

    public static WebElement getElementByCssSelector(String selector) {
        return getElement(By.cssSelector(selector));
    }

    public static void scrollToElement(String elementDataLocator) {
        WebElement element = Driver.getWebDriver().findElement(By.cssSelector("[data-locator='" + elementDataLocator + "']"));
        ((JavascriptExecutor) Driver.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      }

        public static WebElement waitForLinkToHelpDeskElementToLoad() {
          return waitUntilPresent(By.cssSelector("[link-to-help-desk-email]"));
        }
    }

