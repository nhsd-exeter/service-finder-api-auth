package uk.nhs.digital.a2si.servicefinder.integration.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.fail;

public class Driver {

    private static Logger logger = LoggerFactory.getLogger(Driver.class);

    private static WebDriver webDriver;

    private static final String ENV = System.getProperty("ENV");

    public static WebDriver getWebDriver() {
        if (ENV == null) {
            logger.error("You should define your properties correctly, see README.md for solution");
            fail();
        }
        return webDriver = webDriver == null ? createWebDriver() : webDriver;
    }

    private static WebDriver createWebDriver() {
        Properties prop = getProperty();
        String browserUrl = prop.getProperty("local.hub.url");
        logger.info("Selenium hub URL: " + browserUrl);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities("chrome", "", Platform.ANY);
        try {
            return webDriver = new RemoteWebDriver(new URL(browserUrl), desiredCapabilities);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            fail();
            return null;
        }
    }

    public static Properties getProperty() {
        Properties prop = new Properties();
        try (InputStream input = ClassLoader.getSystemResourceAsStream("properties/environment.properties")) {
            System.getProperty("user.dir");
            prop.load(input);
        } catch (IOException e) {
            logger.error("Error reading properties file {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return prop;
    }
}
