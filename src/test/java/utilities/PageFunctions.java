package utilities;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.List;

public class PageFunctions {

    public WebDriverWait wait;
    // Default timeout in seconds for explicit waits. This can be configured.
    private int defaultTimeoutSeconds = 100;

    public static final URL scriptURL = PageFunctions.class.getResource("/axe.min.js");
    public PageFunctions() throws Exception {
        PageFactory.initElements(BrowserConfig.driver, this);
        wait = new WebDriverWait(BrowserConfig.driver, Duration.ofSeconds(20));
    }

    /**
     * Helper method to safely get text from a child element, returning an empty string if not found.
     *
     * @param parentElement The parent WebElement to search within.
     * @param locator       The By locator for the child element.
     * @return The text of the child element, or an empty string if not found.
     */
    public String safelyGetText(WebElement parentElement, By locator) {
        try {
            return parentElement.findElement(locator).getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return ""; // Return empty string if element is not found within the parent
        }
    }

    /**
     * Waits for a single WebElement to become visible on the page.
     *
     * @param element The WebElement to wait for visibility.
     * @return The WebElement once it is visible.
     * @throws AutomationFrameworkException If the element is not found, does not become visible
     * within the timeout, or a WebDriver error occurs.
     */
    public WebElement waitsForWebElement(WebElement element) throws AutomationFrameworkException {
        try {
            // Initialize WebDriverWait with the driver and the specified timeout using Duration (Selenium 4)
            WebDriverWait wait = new WebDriverWait(BrowserConfig.driver, Duration.ofSeconds(defaultTimeoutSeconds));

            // Use ExpectedConditions.visibilityOf to wait until the element is displayed and has size > 0
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            // Delegate exception handling to the common helper method
            return handleWaitExceptions(e, "single WebElement (" + element + ")");
        }
    }


    /**
     * Handles common exceptions that occur during explicit waits and re-throws them
     * as a custom `AutomationFrameworkException` for more structured error reporting.
     *
     * @param e       The original exception caught during the wait operation.
     * @param context A descriptive string indicating where the exception occurred (e.g., "Single WebElement wait").
     * @param <T>     A generic type parameter (used to allow methods to return null or a specific type, though we're throwing here).
     * @throws AutomationFrameworkException A custom exception wrapping the original error
     * with a more user-friendly message.
     */
    private <T> T handleWaitExceptions(Exception e, String context) throws AutomationFrameworkException {
        // In a real project, you would integrate a robust logging framework here (e.g., SLF4J with Log4j/Logback)
        // Example: Logger.error("Error during " + context + ": " + e.getMessage(), e);
        e.printStackTrace(); // For console output during development/debugging

        if (e instanceof NoSuchElementException) {
            throw new AutomationFrameworkException("Element not found in DOM during " + context + ": " + e.getMessage(), e);
        } else if (e instanceof TimeoutException) {
            // TimeoutException often covers cases where an element is not visible within the time
            throw new AutomationFrameworkException("Timeout waiting for " + context + " to become visible within " + defaultTimeoutSeconds + " seconds.", e);
        } else if (e instanceof WebDriverException) {
            // General WebDriver issues not covered by more specific exceptions
            throw new AutomationFrameworkException("WebDriver error during " + context + ": " + e.getMessage(), e);
        } else {
            // Catch-all for any other unexpected exceptions
            throw new AutomationFrameworkException("An unexpected error occurred during " + context + ": " + e.getMessage(), e);
        }
    }
    /**
     * Waits for all WebElements in a list to become visible on the page.
     *
     * @param elements The list of WebElements to wait for visibility.
     * @return The original list of WebElements after all of them are visible.
     * @throws AutomationFrameworkException If any element in the list is not found,
     * does not become visible within the timeout,
     * or a WebDriver error occurs.
     */
    public List<WebElement> waitsForListElement(List<WebElement> elements) throws AutomationFrameworkException {
        try {
            // Initialize WebDriverWait with the driver and the specified timeout using Duration (Selenium 4)
            WebDriverWait wait = new WebDriverWait(BrowserConfig.driver, Duration.ofSeconds(defaultTimeoutSeconds));

            // Use ExpectedConditions.visibilityOfAllElements to wait until all elements in the list are visible
            wait.until(ExpectedConditions.visibilityOfAllElements(elements));
            return elements; // Return the list, indicating success
        } catch (Exception e) {
            // Delegate exception handling to the common helper method
            return handleWaitExceptions(e, "list of WebElements");
        }
    }

    /**
     * Waits for a WebElement to be visible and then performs a click action on it.
     * This method combines waiting for visibility and clicking into a single, robust operation.
     *
     * @param element The WebElement to click.
     * @throws AutomationFrameworkException If the element is not found, not visible,
     *                                      or if the click operation fails for any WebDriver-related reason.
     */
    public void clickElement(WebElement element) throws AutomationFrameworkException {
        try {
            // First, wait for the element to be visible using the existing utility method
            WebElement clickableElement = waitsForWebElement(element);
            // Now that the element is visible, perform the click
            clickableElement.click();
            // No Thread.sleep() here; if you need to wait for something *after* the click,
            // use another explicit wait (e.g., waiting for a new page to load, or an element to disappear).
        } catch (AutomationFrameworkException e) {
            // Re-throw the AutomationFrameworkException caught from waitsForWebElement
            throw new AutomationFrameworkException("Failed to click element after waiting: " + e.getMessage(), e);
        } catch (WebDriverException e) {
            // Catch WebDriver-specific exceptions that might occur during the .click() operation itself
            throw new AutomationFrameworkException("WebDriver error during click operation on element: " + e.getMessage(), e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions during the click process
            throw new AutomationFrameworkException("An unexpected error occurred while clicking element: " + e.getMessage(), e);
        }
    }

    /**
     * Waits for a WebElement to be visible, clears its current value, and then
     * sends a sequence of character to it. This is typically used for input fields.
     *
     * @param element The WebElement (e.g., input field, textarea) to send keys to.
     * @param text    The string of characters to be sent.
     * @throws AutomationFrameworkException If the element is not found, not visible,
     *                                      or if the clear or sendKeys operation fails for any WebDriver-related reason.
     */
    public void sentKeys(WebElement element, String text) throws AutomationFrameworkException {
        try {
            // First, wait for the element to be visible using the existing utility method
            WebElement inputElement = waitsForWebElement(element);
            // Clear any existing text in the input field
            inputElement.clear();
            // Send the desired text to the input field
            inputElement.sendKeys(text);
            // No Thread.sleep() here; if you need to wait for a reaction to the input,
            // use another explicit wait (e.g., waiting for an auto-suggest list to appear).
        } catch (AutomationFrameworkException e) {
            // Re-throw the AutomationFrameworkException caught from waitsForWebElement
            throw new AutomationFrameworkException("Failed to interact with element for sentKeys after waiting: " + e.getMessage(), e);
        } catch (WebDriverException e) {
            // Catch WebDriver-specific exceptions that might occur during the .clear() or .sendKeys() operations
            throw new AutomationFrameworkException("WebDriver error during clear/sendKeys operation on element: " + e.getMessage(), e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions during the sentKeys process
            throw new AutomationFrameworkException("An unexpected error occurred while sending keys to element: " + e.getMessage(), e);
        }
    }

    /**
     * Waits for a dropdown (SELECT element) to be visible and then selects an option
     * based on its visible text.
     *
     * @param dropdownElement The WebElement representing the SELECT HTML tag.
     * @param visibleText     The visible text of the option to select from the dropdown.
     * @throws AutomationFrameworkException If the dropdown element is not found, not visible,
     *                                      or if the option with the specified text cannot be selected.
     */
    public void selectDropdownOptionByVisibleText(WebElement dropdownElement, String visibleText) throws AutomationFrameworkException {
        try {
            // Ensure the dropdown element is visible before interacting with it
            WebElement visibleDropdown = waitsForWebElement(dropdownElement);

            // Initialize the Select object with the visible dropdown element
            Select select = new Select(visibleDropdown);

            // Select the option by its visible text
            select.selectByVisibleText(visibleText);
        } catch (AutomationFrameworkException e) {
            // Re-throw any framework exceptions originating from waitsForWebElement
            throw new AutomationFrameworkException("Failed to select dropdown option after waiting for dropdown: " + e.getMessage(), e);
        } catch (NoSuchElementException e) {
            // This can be thrown by Select if the option is not found
            throw new AutomationFrameworkException("Option with text '" + visibleText + "' not found in dropdown.", e);
        } catch (WebDriverException e) {
            // Catch general WebDriver issues during the select operation
            throw new AutomationFrameworkException("WebDriver error during dropdown selection for text '" + visibleText + "': " + e.getMessage(), e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            throw new AutomationFrameworkException("An unexpected error occurred while selecting dropdown option '" + visibleText + "': " + e.getMessage(), e);
        }
    }

    /**
     * Waits until either of two specified WebElements becomes visible on the page.
     * This is useful for scenarios like waiting for search results or a "no results" message.
     *
     * @param element1 The first WebElement to wait for visibility.
     * @param element2 The second WebElement to wait for visibility.
     * @throws AutomationFrameworkException If neither element becomes visible within the timeout,
     *                                      or if a WebDriver error occurs.
     */
    public void waitForOneOfElementsToBecomeVisible(WebElement element1, WebElement element2) throws AutomationFrameworkException {
        try {
            WebDriverWait wait = new WebDriverWait(BrowserConfig.driver, Duration.ofSeconds(defaultTimeoutSeconds));
            // Use ExpectedConditions.or to wait for either condition to be true
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOf(element1),
                    ExpectedConditions.visibilityOf(element2)
            ));
        } catch (Exception e) {
            // Delegate exception handling to the common helper method.
            handleWaitExceptions(e, "one of elements to become visible (" + element1 + ", " + element2 + ")");
            throw new IllegalStateException("Unreachable code: handleWaitExceptions should always throw an exception.");
        }
    }


    /**
     * Custom exception class for the automation framework.
     * This provides a more specific type of exception for test failures
     * related to element interactions and waits, allowing for more granular
     * error handling and reporting in your test suite.
     */
    public static class AutomationFrameworkException extends Exception {
        /**
         * Constructs a new AutomationFrameworkException with the specified detail message.
         *
         * @param message The detail message (which is saved for later retrieval by the getMessage() method).
         */
        public AutomationFrameworkException(String message) {
            super(message);
        }

        /**
         * Constructs a new AutomationFrameworkException with the specified detail message and cause.
         *
         * @param message The detail message.
         * @param cause   The cause (which is saved for later retrieval by the getCause() method).
         *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
         */
        public AutomationFrameworkException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}
