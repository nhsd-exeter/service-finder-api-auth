package uk.nhs.digital.a2si.servicefinder.integration;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.afterAll;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.rules.TestWatchman;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.digital.a2si.servicefinder.integration.common.Driver;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.AdminTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.ClinicalSearchTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.HelpPageTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.KnownServiceSearchTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.LandingPageTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.LoginSubPagesTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.LoginTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.RegistrationTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.ReportingTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.ReportingToolkitTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.SearchResultsActionsTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.SearchResultsTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.ServiceTypeSearchTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.desktop.MissingServiceTypeFeedbackTests;


@RunWith(Spectrum.class)
public class RunSpectrumTest {

    private static Logger logger = LoggerFactory.getLogger(RunSpectrumTest.class);

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            logger.info("Starting test: " + description.getMethodName());
        }
    };
    //This is the main Spectrum test runner class
    //All 'it' tests should be stored in a relevant class file and called from this class
    {
        // System.setProperty("ENV", "test");
        afterAll(() -> {
            Driver.getWebDriver().quit();
        });

        describe("Login Tests", LoginTests::run);
        describe("Landing Page Tests", LandingPageTests::run);
        describe("Service Type Search Tests", ServiceTypeSearchTests::run);
        describe("Known Service Search Tests", KnownServiceSearchTests::run);
        describe("Clinical Search Tests", ClinicalSearchTests::run);
        describe("Search Results Tests", SearchResultsTests::run);
        describe("Search Result Actions Tests", SearchResultsActionsTests::run);
        describe("Registration Tests", RegistrationTests::run);
        describe("LoginSubPages Tests", LoginSubPagesTests::run);
        describe("Admin Tests", AdminTests::run);
        describe("Reporting Tests", ReportingTests::run);
        describe("Help Page Tests", HelpPageTests::run);
        describe("Missing Service Type Feedback Tests", MissingServiceTypeFeedbackTests::run);
        describe("Reporting Toolkit Tests", ReportingToolkitTests::run);

    }

}
