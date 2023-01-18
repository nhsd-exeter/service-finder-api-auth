package uk.nhs.digital.a2si.servicefinder.integration;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.digital.a2si.servicefinder.integration.common.Driver;
import uk.nhs.digital.a2si.servicefinder.integration.common.Hooks;
import uk.nhs.digital.a2si.servicefinder.integration.helper.PageNavHelper;
import uk.nhs.digital.a2si.servicefinder.integration.helper.RetryTestRunner;
import uk.nhs.digital.a2si.servicefinder.integration.pages.login.LoginPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.registration.RegistrationPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.resendverification.ResendVerificationPage;
import uk.nhs.digital.a2si.servicefinder.integration.pages.resetpassword.ResetPasswordPage;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.afterAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Spectrum.class)
public class RunSanityTest {

    private static Logger logger = LoggerFactory.getLogger(RunSpectrumTest.class);

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            logger.info("Starting test: " + description.getMethodName());
        }
    };

    {

        afterAll(() -> {
            Driver.getWebDriver().quit();
        });

        describe("Sanity Tests", () -> {

            RetryTestRunner.RunnableBlock before = () -> {
              Driver.getWebDriver().manage().deleteCookieNamed("user-has-accepted-cookies");
              Driver.getWebDriver().manage().deleteCookieNamed("user-has-dismissed-registration-modal");
          };

            RetryTestRunner.RunnableBlock after = Hooks::captureScreenshot;

            RetryTestRunner retryTestRunner = new RetryTestRunner(before, after);

            it("Password reset page can be reached", () -> {
                retryTestRunner.run(() -> {
                    PageNavHelper.navigateToLoginPageAcceptCookiePolicy();
                    LoginPage.navigateToResetPasswordPage();
                    assertThat(ResetPasswordPage.isEmailAddressInputFieldDisplay(), is(true));
                    PageNavHelper.navigateToLandingPage();
                    });
                });

            it("Resent verification link page can be reached", () -> {
                retryTestRunner.run(() -> {
                    PageNavHelper.navigateToLoginPageAcceptCookiePolicy();
                    LoginPage.navigateToResendVerificationLinkPage();
                    assertThat(ResendVerificationPage.isEmailAddressInputFieldDisplay(), is(true));
                    PageNavHelper.navigateToLandingPage();
                });
            });

            it("Registration page can be reached", () -> {
                retryTestRunner.run(() -> {
                    PageNavHelper.navigateToLoginPageAcceptCookiePolicy();
                    LoginPage.navigateToRegistrationPage();
                    RegistrationPage.onRegistrationPage();
                    PageNavHelper.navigateToLandingPage();
                });
            });

        });
    }
}
