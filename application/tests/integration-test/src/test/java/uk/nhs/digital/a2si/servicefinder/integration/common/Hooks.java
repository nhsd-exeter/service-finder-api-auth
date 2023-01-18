package uk.nhs.digital.a2si.servicefinder.integration.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.a2si.servicefinder.integration.pages.login.LoginPage;

import java.io.File;
import java.io.IOException;


@Slf4j

public class Hooks extends EventFiringWebDriver {

    private static Logger logger = LoggerFactory.getLogger(Hooks.class);

    public Hooks() {
        super(Driver.getWebDriver());
    }

    public static void loginSearchUser() {
        processLogin("search@nhs.net", "password", "");
    }

    public static void loginAdminUser() {
        processLogin("admin@nhs.net", "password", "ADMIN_");
        log.info("Login as Admin user");
    }

    public static void loginApproverUser() {
        processLogin("approver@nhs.net", "password", "APPROVER_");
    }

    public static void loginReporterUser() {
        processLogin("reporter@nhs.net", "password", "REPORTER_");
    }

    private static void processLogin(String emailAddress, String password, String envPrefix) {
        if (!System.getProperty("ENV").equals("test")) {
            emailAddress = "service-finder-admin@nhs.net";
            password = System.getProperty("envpassword");
            logger.info("Email address =  " + emailAddress);
            logger.info("Password =  " + password);
        }

        LoginPage.visit();
        LoginPage.acceptCookiePolicy();
        LoginPage.waitUntilLoaded();
        LoginPage.login(emailAddress, password);
    }

    public static void logout() {
        try {
            captureScreenshot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Driver.getWebDriver().navigate().refresh();
        LoginPage.logout();
    }

    public static void mobLogout() {
        try {
            captureScreenshot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Driver.getWebDriver().navigate().refresh();
        LoginPage.expandMobMenu();
        LoginPage.logout();
    }

    public static void captureScreenshot() throws IOException {
        captureScreenshot(null);
    }

    public static void captureScreenshot(String suffix) throws IOException {
        String directory = "build/reports/tests/screenshots/";

        try {
            File screenshot = new EventFiringWebDriver(Driver.getWebDriver()).getScreenshotAs(OutputType.FILE);
            String fileName = System.currentTimeMillis() + (suffix == null ? "" : suffix) + ".png";
            FileUtils.copyFile(screenshot, new File(directory + fileName));
        } catch (WebDriverException somePlatformsDontSupportScreenshots) {
            logger.error("Couldn't embed screenshot" + somePlatformsDontSupportScreenshots.getMessage());
        }
    }

    public static void mobileMode(){
        Driver.getWebDriver().manage().window().setSize(new Dimension(300, 900));
    }

}
