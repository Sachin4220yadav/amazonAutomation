package base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Base {
    public static final Logger log = LogManager.getLogger(Base.class);
    public static WebDriver driver;
    public Properties prop;

    public void loadProperties() {
        if (prop == null) {
            prop = new Properties();
            try {
                log.info("Loading data.properties file...");
                FileInputStream fis = new FileInputStream("src/main/java/data.properties");
                prop.load(fis);
                log.info("Properties file loaded successfully.");
            } catch (IOException e) {
                log.error("Failed to load properties file: " + e.getMessage(), e);
            }
        }
    }
    
    public void setDriver(WebDriver d) {
        driver = d;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getBrowser() {
        loadProperties();
        return prop.getProperty("browser", "chrome");
    }

    public String getUrl() {
        loadProperties();
        return prop.getProperty("url");
    }

    public WebDriver initializeDriver() {
        log.info("Initializing ChromeDriver...");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        log.info("ChromeDriver initialized and maximized.");
        return driver;
    }
}

