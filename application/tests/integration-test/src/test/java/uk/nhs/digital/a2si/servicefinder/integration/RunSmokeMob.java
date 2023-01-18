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
import uk.nhs.digital.a2si.servicefinder.integration.pages.location.LocationPage;

import static com.greghaskins.spectrum.Spectrum.afterEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.afterAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Spectrum.class)
public class RunSmokeMob {

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

        describe("Mobile Smoke Tests", () -> {
            afterEach(Hooks::mobLogout);

            it("user should be able to login", () -> {
                Hooks.mobileMode();
                Hooks.loginSearchUser();
                assertThat(LocationPage.onLocationPage(), is(true));
            });

            it("user should be able to perform a service type search", () -> {
                Hooks.loginSearchUser();
                PageNavHelper.navigateToStsResultsPage("dos");
                assertThat(PageNavHelper.onSearchResultsPage(), is(true));
            });

            it("user should be able to perform a known service search", () -> {
                Hooks.loginSearchUser();
                PageNavHelper.navigateToKssResultsPageNoClick();
                assertThat(PageNavHelper.onSearchResultsPage(), is(true));
            });
        });
    }
}
