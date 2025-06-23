package runner;
import io.cucumber.core.cli.Main;

import java.util.Arrays;
import java.util.stream.Stream;


public class TestRunner extends Main {
    private static String[] defaultOptions = {
            "--glue", "stepDefinitions",
            "--plugin", "pretty",
            "--plugin","html:cucumber-reports/cucumber-html-report.html",
            "resources/features"
    };

    public static void main(String args[]) {
        args = Stream.concat(Arrays.stream(defaultOptions), Arrays.stream(args))
                .toArray(String[]::new);
        run(args, Thread.currentThread().getContextClassLoader());
        System.exit(0);
    }
}


