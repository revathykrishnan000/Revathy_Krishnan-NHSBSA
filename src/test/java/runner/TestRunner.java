package runner;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
    @RunWith(Cucumber.class)
    @CucumberOptions(
            features = "src\\test\\resources\\features", // Path to feature files
            glue = "stepDefinitions",                          // Path to step definitions
            plugin = {"pretty",                      // Pretty console output
                    "html:target/cucumber-reports/cucumber-html-report.html", // HTML report
                    "json:target/cucumber-reports/cucumber.json",           // JSON report
                    "junit:target/cucumber-reports/cucumber.xml"},          // JUnit XML report
            monochrome = true,                       // Readable console output
            snippets = CucumberOptions.SnippetType.CAMELCASE, // Generate camelCase snippets
            dryRun = false                           // Set to true to check for unimplemented steps
    )
    public class TestRunner {
    }

