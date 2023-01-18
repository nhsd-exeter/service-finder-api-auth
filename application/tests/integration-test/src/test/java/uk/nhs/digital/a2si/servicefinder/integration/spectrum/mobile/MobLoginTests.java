package uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile;

import com.greghaskins.spectrum.Spectrum;
import java.util.Properties;
import org.junit.runner.RunWith;
import uk.nhs.digital.a2si.servicefinder.integration.common.Driver;
import uk.nhs.digital.a2si.servicefinder.integration.common.Hooks;
import uk.nhs.digital.a2si.servicefinder.integration.pages.landing.LandingPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.location.LocationPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.login.LoginPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.search.ServiceTypeSearchPage;
import uk.nhs.digital.a2si.servicefinder.integration.helper.CommonHelper;
import uk.nhs.digital.a2si.servicefinder.integration.helper.LoginHelper;
import uk.nhs.digital.a2si.servicefinder.integration.helper.PageNavHelper;
import uk.nhs.digital.a2si.servicefinder.integration.helper.RetryTestRunner;
import uk.nhs.digital.a2si.servicefinder.integration.pages.login.LoginPageElements;

import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@RunWith(Spectrum.class)
public class MobLoginTests {

    public static void run() {

        RetryTestRunner.RunnableBlock before = () -> {
            Hooks.mobileMode();
            Driver.getWebDriver().manage().deleteCookieNamed("user-has-accepted-cookies");
            Driver.getWebDriver().manage().deleteCookieNamed("user-has-dismissed-registration-modal");
        };

        RetryTestRunner.RunnableBlock after = Hooks::captureScreenshot;

        RetryTestRunner retryTestRunner = new RetryTestRunner(before, after);

        it("Invalid details should show an alert", () -> {
            retryTestRunner.run(() -> {
                LoginHelper.invalidLoginAttempt();
                assertThat(LoginHelper.loginAlertDisplayed(), is(true));
                assertThat(CommonHelper.getPageText(), containsString("Enter the correct email"));
                LoginPage.navigateToResetPasswordPage();
                assertThat(CommonHelper.getPageText(), not(containsString("Invalid credentials")));
                LandingPage.returnToLandingPage();
            });
        });

        it("Password reveal change password to text", () -> {
            retryTestRunner.run(() -> {
                LoginPage.checkPasswordReveal();
                assertThat(LoginHelper.isPasswordTextRevealed(), is(true));
            });
        });

        it("Cookie policy button should be shown", () -> {
            retryTestRunner.run(() -> {
                PageNavHelper.navigateToLoginPage();
                assertThat(LoginHelper.cookiePolicyIsDisplayed(), is(true));
            });
        });

        it("When I select the link to cookie policy via the cookie modal then I'm taken to the cookie policy page", () -> {
          retryTestRunner.run(() -> {
            PageNavHelper.navigateToLoginPage();
            LoginPage.selectCookiePolicyLinkFromCookieModal();
            assertThat(CommonHelper.getPageText(), containsString("Cookie Policy"));
          });
        });

        it("When I select the opt out of analytics link then I am returned to the login screen", () -> {
          retryTestRunner.run(() -> {
            PageNavHelper.navigateToLoginPage();
            LoginPage.selectOptOutOfAnalyticsCookies();
            assertThat(LoginHelper.loginFormDisplayed(), is(true));
          });
        });


        it("Login form should be shown", () -> {
            retryTestRunner.run(() -> {
                PageNavHelper.navigateToLoginPageAcceptCookiePolicy();
                assertThat(LoginHelper.loginFormDisplayed(), is(true));
            });
        });

        it("A logged in user can refresh their session and then continue", () -> {
            retryTestRunner.run(() -> {
              Hooks.loginSearchUser();
              LocationPage.onLocationPage();
              LoginPage.refreshPage();
              assertThat(LocationPage.onLocationPage(), is(true));
              Hooks.mobLogout();
            });
        });

        it("Remember me tick box is checked as default and can be unchecked", () -> {
            retryTestRunner.run(() -> {
                PageNavHelper.navigateToLoginPageAcceptCookiePolicy();
                assertThat(LoginHelper.rememberEmailCheckBoxDisplayed(), is(true));
                LoginPage.rememberEmailCheckBoxCanBeUnticked();
                assertThat(LoginHelper.rememberEmailCheckBoxCanBeUnticked(), is(true));
                LoginPage.rememberEmailCanBeReticked();
                assertThat(LoginHelper.rememberEmailCheckBoxCanBeTicked(), is(true));
            });
        });

        it("Should be able to see the add to homepage banner on mobile", () -> {
            retryTestRunner.run(() -> {
                PageNavHelper.navigateToLoginPageAcceptCookiePolicy();
                Driver.getWebDriver().navigate().refresh();
                assertThat(LoginPage.isMobileBannerAvailable(), is(true));
            });
        });

        it("Should be able to see service unavailable page and return to home page", () -> {
            retryTestRunner.run(() -> {
                PageNavHelper.navigateToServiceUnavailablePage();
                assertThat(CommonHelper.getPageText(), containsString(PageNavHelper.SERVICE_UNAVAILABLE_TEXT));
                PageNavHelper.navigateToLandingPage();
            });
        });

        it("User should be able to see unreachable page and return to login page", () -> {
            retryTestRunner.run(() -> {
                Hooks.loginSearchUser();
                LocationPage.onLocationPage();
                PageNavHelper.navigateToUnreachablePage();
                LandingPage.waitForUnreachablePageToLoad();
                assertThat(CommonHelper.getPageText(), containsString(PageNavHelper.UNREACHABLE_PAGE));
                Hooks.mobLogout();
            });
        });

        // To be removed once URL has be released

        it( "Given I have landed on the old URL when I select the link I arrive at the new URL",
      () -> {
        retryTestRunner.run(
            () -> {
              Properties prop = Driver.getProperty();
              String url = prop.getProperty("old_nonprodUrl");
              System.out.println(url);
              LoginPageElements.goTo(url);
              assertThat(CommonHelper.getPageText(), containsString(PageNavHelper.OLD_URL_TEXT));
              LoginPage.acceptCookiePolicy();
              LoginPage.selectNewURL();
              assertThat(LoginHelper.rememberEmailCheckBoxDisplayed(), is(true));
              Hooks.loginSearchUser();
              LocationPage.onLocationPage();
              Hooks.mobLogout();
            });
      });
    }
}
