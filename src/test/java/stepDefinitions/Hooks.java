package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utilities.BrowserConfig;


public class Hooks {
    @Before
    public void beforeScenario() throws Exception {
        System.out.println("Scenario Started");
        BrowserConfig.setUp();
        System.out.println("Browser Launched");
    }

    @After
    public void afterScenario(Scenario scenario) throws Exception {
        //Screenshot on failed scenarios and attach to the cucumber report
        if (scenario.isFailed()) {
            try {
                // Check if the driver supports taking screenshots
                if (BrowserConfig.driver instanceof TakesScreenshot) {
                    final byte[] screenshot = ((TakesScreenshot) BrowserConfig.driver).getScreenshotAs(OutputType.BYTES);
                    // Attach the screenshot to the report
                    scenario.attach(screenshot, "image/png", "Screenshot on Failure: " + scenario.getName());
                } else {
                    System.err.println("WebDriver does not support taking screenshots.");
                }
            } catch (Exception e) {
                System.err.println("Failed to take screenshot: " + e.getMessage());
                e.printStackTrace();
            }
        }
        BrowserConfig.tearDown();//Close the window
        System.out.println("Browser Closed");
    }
}
