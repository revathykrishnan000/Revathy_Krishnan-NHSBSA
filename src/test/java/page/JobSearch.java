package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.BrowserConfig;
import utilities.PageFunctions;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JobSearch extends PageFunctions {


    // Locators for elements on the search page
    @FindBy(xpath = "//a[contains(text(),'Search')]")
    private WebElement searchLink;// To locate search link
    @FindBy(xpath = "//input[@id='keyword']")
    private WebElement keywordsInput;// To locate job title / skills input
    @FindBy(xpath = "//input[@id='location']")
    private WebElement locationInput;// To locate location input
    @FindBy(xpath = "//select[@id='distance']")
    private WebElement distanceDropdown;// To locate distance dropdown input
    @FindBy(xpath = "//a[contains(text(),'More search options')]")
    private WebElement advancedSearch;// To locate advanced search link
    @FindBy(xpath = "//input[@id='jobReference']")
    private WebElement jobReferenceInput;// To locate job Reference Input
    @FindBy(xpath = "//input[@id='employer']")
    private WebElement employerInput;// To locate employer Input
    @FindBy(xpath = "//select[@id='payRange']")
    private WebElement payRangeDropdown;// To locate salary Range Input
    @FindBy(xpath = "//input[@id='search']")
    private WebElement searchButton;// To locate search Button
    @FindBy(xpath = "//select[@id='sort']")
    private WebElement sortByDropdown;// To locate sort by drop down
    @FindBys(@FindBy(xpath = "//h3[contains(text(),'No result')]"))
    private List<WebElement> noResultsMessageList;// To locate no result message
    @FindBy(xpath = "//h3[contains(text(),'No result')]")
    private WebElement noResultsMessage;
    @FindBy(css = "ul.nhsuk-list.search-results")//To locate search result container
    private WebElement searchResultsContainer;
    @FindBys(@FindBy(css = "li.nhsuk-list-panel.search-result.nhsuk-u-padding-3")) //To locate search result cards
    private List<WebElement> searchResultCardsList;
    @FindBys(@FindBy(xpath = ".//li[contains(text(),'Date posted')]/strong"))//To locate search result container
    private List<WebElement> datePostedInResult;
    // Locators for details *within* a single job result card (relative to searchResultCards)
    private By jobTitleInResult = By.cssSelector("h2.nhsuk-heading-m a");
    private By employerInResult = By.cssSelector("h3.nhsuk-u-font-weight-bold");
    private By locationInResult = By.cssSelector("h3.nhsuk-u-font-weight-bold div.location-font-size");
    private By distanceInResult = By.xpath(".//li[contains(text(),'Distance')]/strong");
    private By payRangeInResult = By.xpath(".//li[contains(text(),'Salary')]/strong");
    private By contractTypeInResult = By.xpath(".//li[contains(text(),'Contract type')]/strong");
    private By workingPatternInResult = By.xpath(".//li[contains(text(),'Working pattern')]/strong");

    /**
     * Record to hold details extracted from a single job search result.
     * Using a record for conciseness as it's a simple data carrier.
     */
    public record JobSearchResult(
            String title,
            String employer,
            String location,
            String distance,
            String payRange,
            String contractType,
            String workingPattern
    ) {
    }

    /**
     * Constructor to initialize the WebDriver and WebDriverWait.
     * /** @param driver The WebDriver instance.
     */
    public JobSearch() throws Exception {
        PageFactory.initElements(BrowserConfig.driver, this);
        wait = new WebDriverWait(BrowserConfig.driver, Duration.ofSeconds(20));
    }

    /**
     * Navigates to the NHS Jobs search page.
     */
    public void navigateToSearchPage() {
        try {
            clickElement(searchLink);
        } catch (Exception e) {
            System.out.println("Search link not found in the home page");
        }
    }

    /**
     * Enters search keywords (job title/skills) into the keywords input field.
     *
     * @param keywords The keywords to enter.
     */
    public void enterKeywords(String keywords) throws AutomationFrameworkException {
        if (!keywords.isEmpty()) {
            sentKeys(keywordsInput, keywords);

        }
    }

    /**
     * Enters a location (post code/town city) into the location input field.
     *
     * @param location The location to enter.
     */
    public void enterLocation(String location) throws AutomationFrameworkException {
        if (!location.isEmpty()) {
            sentKeys(locationInput, location);
        }

    }

    /**
     * Selects a distance from the distance dropdown.
     *
     * @param distance The visible text of the distance option (e.g., "5 miles", "10 miles").
     */
    public void selectDistance(String distance) throws AutomationFrameworkException {
        selectDropdownOptionByVisibleText(distanceDropdown, distance);

    }

    /**
     * Enters an employer name into the employer input field.
     *
     * @param employer The employer name to enter.
     */
    public void enterEmployer(String employer) throws AutomationFrameworkException {
        if (!employer.isEmpty()) {
            sentKeys(employerInput, employer);
        }
    }

    /**
     * Selects a pay range from the pay range dropdown.
     *
     * @param payRange The visible text of the pay range option (e.g., "£20,000 - £25,000").
     */
    public void selectPayRange(String payRange) throws AutomationFrameworkException {
        if (!payRange.isEmpty()) {
            selectDropdownOptionByVisibleText(payRangeDropdown, payRange);
        }
    }

    /**
     * Clicks the more search options link.
     */
    public void clickMoreSearchOptions() {
        try {
            clickElement(advancedSearch);
        } catch (Exception e) {
            System.out.println("More search options link is not available ");
        }
    }

    /**
     * Clicks the search button.
     */
    public void clickSearchButton() throws AutomationFrameworkException {
        clickElement(searchButton);
        // Wait for search results to load or no results message to appear
        waitForOneOfElementsToBecomeVisible(searchResultsContainer, noResultsMessage);

    }

    /**
     * Selects a sort by from the pay range dropdown.
     *
     * @param sortBy The visible text of the sort by option (e.g., "Date Posted (Newest)").
     */
    public void selectSortBySearch(String sortBy) throws AutomationFrameworkException {
        selectDropdownOptionByVisibleText(sortByDropdown, sortBy);
    }
    /**
     * Gets all details for each job displayed in the search results.
     * Iterates through each job result card and extracts title, employer, location,
     * pay range,  distance, and date posted.
     *
     * @return A list of JobSearchResult objects, each containing full details of a job.
     * Returns an empty list if no results are found or if the no results message is displayed.
     */
    public List<JobSearchResult> getSearchResultsDetails() throws AutomationFrameworkException {
        // Check if no results message is displayed first
        if (!noResultsMessageList.isEmpty()) {
            return List.of(); // Return empty list if no results
        }

        // Wait for at least one search result card to be visible
        waitsForWebElement(searchResultCardsList.getFirst());
        wait.until(ExpectedConditions.visibilityOf(searchResultCardsList.getFirst()));

        List<JobSearchResult> results = new ArrayList<>();
        List<WebElement> jobElements = searchResultCardsList;

        for (WebElement jobElement : jobElements) {
            String title = safelyGetText(jobElement, jobTitleInResult);
            String employer = safelyGetText(jobElement, employerInResult);
            String location = safelyGetText(jobElement, locationInResult);
            String distance = safelyGetText(jobElement, distanceInResult);
            if (!distance.isEmpty() && !location.isEmpty()) {
                distance = distance.replaceAll("(?i)\\b(miles|mile|mi|m)\\b\\s*", "");
            }
            String payRange = safelyGetText(jobElement, payRangeInResult);
            String contractType = safelyGetText(jobElement, contractTypeInResult);
            String workingPattern = safelyGetText(jobElement, workingPatternInResult);
            results.add(new JobSearchResult(title, employer, location, distance, payRange,contractType,workingPattern));
        }
        return results;
    }


    /**
     * Gets the "Date Posted" text for all job results.
     *
     * @return A list of date posted strings.
     */
    public List<String> getJobResultDates() throws AutomationFrameworkException {
        // Check if no results message is displayed first
        if (!noResultsMessageList.isEmpty()) {
            return List.of(); // Return empty list if no results
        }
        waitsForListElement(datePostedInResult);
        List<WebElement> dateElements = datePostedInResult;

        List<String> dateTexts = new ArrayList<>();
        for (WebElement element : dateElements) {
            dateTexts.add(element.getText());
        }
        return dateTexts;
    }

    /**
     * Parses a pay range string (e.g., "£25,000 to £30,000", "$50k - $60k", "40000")
     * into a low and high value.
     * Handles various formats, currency symbols, and 'k' for thousands.
     *
     * @param payRangeString The string representing the pay range.
     * @return A double array {lowValue, highValue}. If it's a single value, lowValue == highValue.
     * Returns {0.0, 0.0} if parsing fails or range is invalid.
     */
    public static double[] parsePayRange(String payRangeString) {
        if (payRangeString == null || payRangeString.trim().isEmpty()) {
            return new double[]{0.0, 0.0};
        }

        // Normalize string: remove currency symbols, commas, and convert 'k' to '000'
        String normalized = payRangeString.toLowerCase(Locale.ROOT)
                .replaceAll("[£$€,]", "") // Remove currency and commas
                .replaceAll("k", "000") // Convert 'k' to '000'
                .trim();

        // Regex to find numbers. It tries to capture one or two numbers separated by common range indicators.
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(?:to|-|–|and)?\\s*(\\d*(\\.\\d+)?)?");
        Matcher matcher = pattern.matcher(normalized);

        double low = 0.0;
        double high = 0.0;

        if (matcher.find()) {
            try {
                low = Double.parseDouble(matcher.group(1));
                if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                    high = Double.parseDouble(matcher.group(3));
                } else {
                    high = low; // Single value
                }
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse numbers from: " + payRangeString + " -> " + normalized);
            }
        }
        return new double[]{Math.min(low, high), Math.max(low, high)}; // Ensure low is always <= high
    }


    /**
     * Parses a distance string (assumed to be in miles) and extracts the numerical value.
     * Handles formats like "5 miles", "10", "1.5mi", "0.5m", "More than 50 miles".
     *
     * @param distanceString The string representing the distance.
     * @return The distance value as a double, or -1.0 if parsing fails.
     */
    public static double parseMilesDistance(String distanceString) {
        if (distanceString == null || distanceString.trim().isEmpty()) {
            return -1.0; // Indicate failure
        }

        // Regex to find the first sequence of digits potentially with a decimal point
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(distanceString.trim());

        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                // Log or print an error if you want to know which strings failed parsing
                System.err.println("Failed to parse number from distance string (expected miles): '" + distanceString + "'");
            }
        }
        return -1.0; // Parsing failed or no number found
    }

    /**
     * Parses a single date string from job results into a LocalDate object.
     * Handles relative dates ("today", "yesterday", "X days ago") and absolute dates ("dd MMMM yyyy").
     *
     * @param dateString The raw date string from the job result, e.g., "Posted today", "Posted 5 days ago", "Posted 20 June 2025".
     * @return A LocalDate object representing the parsed date, or null if parsing fails or the format is unrecognized.
     */
    public static LocalDate parseJobDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }


        if (dateString.equalsIgnoreCase("today")) {
            return LocalDate.now(); // Current system date
        } else if (dateString.equalsIgnoreCase("yesterday")) {
            return LocalDate.now().minusDays(1); // Day before current system date
        } else if (dateString.contains("days ago")) {
            // Extracts the number from "X days ago" and subtracts it from today
            String[] parts = dateString.split(" ");
            if (parts.length > 0 && parts[0].matches("\\d+")) {
                try {
                    int days = Integer.parseInt(parts[0]);
                    return LocalDate.now().minusDays(days);
                } catch (NumberFormatException e) {
                    System.err.println("WARN: Could not parse number of days from 'days ago' format: '" + dateString + "'");
                    return null; // Parsing failed for days
                }
            } else {
                System.err.println("WARN: Unrecognized 'days ago' format (missing/invalid number): '" + dateString + "'");
                return null; // Format mismatch for days ago
            }
        } else {
            // Attempt to parse as an absolute date like "20 June 2025"
            try {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));
            } catch (DateTimeParseException e) {
                System.err.println("WARN: Unrecognized date format for absolute date: '" + dateString + "'. Error: " + e.getMessage());
                return null; // Parsing failed for absolute date
            }
        }
    }
}
