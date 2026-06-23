package hooks;

import base.Base;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import java.util.HashMap;

public class ScenarioHook extends Base {

    @Before
    public void setup(Scenario scenario) {
        // Only create driver if it doesn't exist yet
        if (getDriver() == null) {
            log.info("Starting browser for: " + scenario.getName());
            try {
                String browser = this.getBrowser();
                if (browser.equals("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    java.util.Map<String, Object> prefs = new HashMap<>();
                    prefs.put("profile.default_content_setting_values.geolocation", 2);
                    prefs.put("profile.default_content_setting_values.notifications", 2);
                    options.setExperimentalOption("prefs", prefs);
                    options.addArguments("--disable-popup-blocking");
                    options.addArguments("--disable-infobars");
                    driver = new ChromeDriver(options);
                    setDriver(driver);
                }
                getDriver().manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(10));
                getDriver().manage().window().maximize();
                log.info("Browser started.");
            } catch (Exception e) {
                log.error("Exception in setup: ", e);
            }
        } else {
            log.info("Reusing existing browser for: " + scenario.getName());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (getDriver() != null) {
                byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(screenshot), ".png");
            }
        } catch (Exception e) {
            log.error("Screenshot failed:", e);
        }
        // Browser is intentionally left open between scenarios and reused.
        log.info("Scenario finished: " + scenario.getName());
    }
}
