package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class BrowserConfig {
    public static WebDriver driver;
    public static PropertyReaderUtil readFile;

    public static void setUp() throws Exception {
        readFile = new PropertyReaderUtil();
        String browserName = System.getProperty("browserName");
        String environmentLink = System.getProperty("environmentLink");
        if(environmentLink==null)
            environmentLink ="HOST_ENV_OPERATOR";
        if (browserName == null)
            browserName = "Firefox";
        System.out.println("Running on " + browserName);
        switch (browserName) {
            case "Firefox":
                //Implementing Firefox with gecko driver
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new FirefoxDriver(firefoxOptions);
                // Maximize and launch the browser
                driver.manage().window().maximize();
                driver.manage().deleteAllCookies();
                break;
            case "Chrome":
                //Implementing chrome with chrome driver
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                // Maximize and launch the browser
                driver.manage().window().maximize();
                driver.manage().deleteAllCookies();
                break;
            case "HeadlessFirefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions headlessFirefoxOptions = new FirefoxOptions();
                headlessFirefoxOptions.addArguments("-headless"); // Run Firefox in headless mode
                driver = new FirefoxDriver(headlessFirefoxOptions);
            case "HeadlessChrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions headlessChromeoptions = new ChromeOptions();
                headlessChromeoptions.addArguments("--headless"); // Run Chrome in headless mode
                headlessChromeoptions.addArguments("--disable-gpu"); // Required for headless on some systems
                headlessChromeoptions.addArguments("--window-size=1920,1080"); // Set window size for consistent screenshots
                headlessChromeoptions.addArguments("--no-sandbox"); // Bypass OS security model, needed for some CI/CD environments
                headlessChromeoptions.addArguments("--disable-dev-shm-usage"); // Overcomes limited resource problems
                driver = new ChromeDriver(headlessChromeoptions);
        }

        String HOST_ENV_OPERATOR = readFile.readFileData(environmentLink);
        driver.navigate().to(HOST_ENV_OPERATOR);
        System.out.println(driver.getCurrentUrl());
    }

    public static void tearDown() throws Exception {
        try {
            driver.quit();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Not able to quit the browser");
        }

    }

}
