package uk.nhs.digital.a2si.servicefinder.integration;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.fdescribe;
import static com.greghaskins.spectrum.dsl.specification.Specification.afterAll;

import com.greghaskins.spectrum.Spectrum;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.digital.a2si.servicefinder.integration.common.Driver;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobClinicalSearchTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobHelpPageTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobKnownServiceSearchTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobLandingPageTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobLoginSubPagesTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobLoginTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobRegistrationTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobReportingTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobReportingToolkitTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobSearchResultsActionsTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobSearchResultsTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobServiceTypeSearchTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobAdminTests;
import uk.nhs.digital.a2si.servicefinder.integration.spectrum.mobile.MobMissingServiceTypeFeedbackTests;

@RunWith(Spectrum.class)
public class RunSpectrumMob {

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

        afterAll(() -> {
            Driver.getWebDriver().quit();
        });

        describe("Mobile Login Tests", MobLoginTests::run);
        describe("Mobile Landing Page Tests", MobLandingPageTests::run);
        describe("Mobile Service Type Search Tests", MobServiceTypeSearchTests::run);
        describe("Mobile Known Service Search Tests", MobKnownServiceSearchTests::run);
        describe("Mobile Clinical Search Tests", MobClinicalSearchTests::run);
        describe("Mobile Search Results Tests", MobSearchResultsTests::run);
        describe("Mobile Search Result Actions Tests", MobSearchResultsActionsTests::run);
        describe("Mobile Registration Tests", MobRegistrationTests::run);
        describe("Mobile LoginSubPages Tests", MobLoginSubPagesTests::run);
        describe("Mobile Admin Tests", MobAdminTests::run);
        describe("Mobile Reporting Tests", MobReportingTests::run);
        describe("Mobile Reporting Toolkit Tests", MobReportingToolkitTests::run);
        describe("Missing Service Type Feedback Tests", MobMissingServiceTypeFeedbackTests::run);
        describe("Mobile Help Page Tests", MobHelpPageTests::run);

    }

}
