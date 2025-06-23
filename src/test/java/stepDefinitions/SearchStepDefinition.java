package stepDefinitions;

import com.deque.axe.AXE;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import page.JobSearch;
import utilities.BrowserConfig;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;
import static page.JobSearch.*;

public class SearchStepDefinition {
    JobSearch nhsJobSearchPage = new JobSearch();

    public SearchStepDefinition() throws Exception {
        super();
    }


    @Given("I am a jobseeker on the NHS Jobs website")
    public void iAmAJobseekerOnTheNhsJobsWebsite() {
        nhsJobSearchPage.navigateToSearchPage();
        assertTrue("NHS Jobs search page should be displayed", BrowserConfig.driver.getTitle().contains("Search for jobs"));
    }

    @When("I search for jobs with job title or skills {string}")
    public void iSearchForJobsWithJobTitleOrSkills(String keywords) throws AutomationFrameworkException {
        nhsJobSearchPage.enterKeywords(keywords);
    }

    @And("my location is {string}")
    public void myLocationIs(String location) throws AutomationFrameworkException {
        nhsJobSearchPage.enterLocation(location);
    }

    @And("I prefer a distance of {string} from the location {string}")
    public void iPreferADistanceAndLocation(String distance, String location) throws AutomationFrameworkException {
        if (distance.isEmpty()) {
            System.out.println("Distance is empty");
        } else if (!location.isEmpty()) {
            nhsJobSearchPage.selectDistance(distance);
        } else {
            assertFalse("Error with the test data : Distance should be empty when location is empty", location.isEmpty());
        }

    }

    @And("I click more search options link")
    public void iClickMoreSearchOptionsLink() {
        nhsJobSearchPage.clickMoreSearchOptions();
    }

    @And("the employer is {string}")
    public void theEmployerIs(String employer) throws AutomationFrameworkException {
        nhsJobSearchPage.enterEmployer(employer);
    }

    @And("the pay range is {string}")
    public void thePayRangeIs(String payRange) throws AutomationFrameworkException {
        nhsJobSearchPage.selectPayRange(payRange);
    }

    @And("I click the Search button")
    public void iClickTheSearchButton() throws AutomationFrameworkException {
        nhsJobSearchPage.clickSearchButton();
    }


    @Then("I should see a list of jobs matching keyword: {string},location: {string},distance: {string},employer: {string} and pay range: {string}")
    public void iShouldSeeAListOfJobsMatchingAnd(String keyword, String location, String distance, String employer, String payRange) throws AutomationFrameworkException {
        List<JobSearch.JobSearchResult> jobResults = nhsJobSearchPage.getSearchResultsDetails();
        assertFalse("Job results list is empty", jobResults.isEmpty());
        boolean keywordMatch = jobResults.stream().anyMatch(result -> {
            // Get all relevant fields from the current JobSearchResult, convert to lowercase
            String resultTitleLower = result.title() != null ? result.title().toLowerCase(Locale.ROOT) : "";
            String resultEmployerLower = result.employer() != null ? result.employer().toLowerCase(Locale.ROOT) : "";
            String resultLocationLower = result.location() != null ? result.location().toLowerCase(Locale.ROOT) : "";
            String resultDistanceLower = result.distance() != null ? result.distance().toLowerCase(Locale.ROOT) : "";
            String resultPayRangeLower = result.payRange() != null ? result.payRange().toLowerCase(Locale.ROOT) : "";
            String resultContractTypeLower = result.contractType() != null ? result.contractType().toLowerCase(Locale.ROOT) : "";
            String resultWorkingPatternLower = result.workingPattern() != null ? result.workingPattern().toLowerCase(Locale.ROOT) : "";


            // Check if the searchKeyword is contained in any of these fields
            return resultTitleLower.contains(keyword.toLowerCase(Locale.ROOT))||
                    resultEmployerLower.contains(keyword.toLowerCase(Locale.ROOT)) ||
                    resultLocationLower.contains(keyword.toLowerCase(Locale.ROOT)) ||
                    resultDistanceLower.contains(keyword.toLowerCase(Locale.ROOT)) ||
                    resultPayRangeLower.contains(keyword.toLowerCase(Locale.ROOT)) ||
                    resultContractTypeLower.contains(keyword.toLowerCase(Locale.ROOT)) ||
                    resultWorkingPatternLower.contains(keyword.toLowerCase(Locale.ROOT));
        });
        assertTrue("At least one search details should contain the word: " + keyword, keywordMatch);
        boolean locationMatch = jobResults.stream().anyMatch(result -> result.location().toLowerCase(Locale.ROOT).contains(location.toLowerCase(Locale.ROOT)));
        assertTrue("At least one location should contain the word: " + location, locationMatch);
        if (!distance.isEmpty() && !location.isEmpty()) {
            distance = distance.replaceAll("(?i)\\b(miles|mile|mi|m)\\b\\s*", "");
            String finalDistance = distance;
            boolean distanceMatch = jobResults.stream()
                    .allMatch(result -> {
                        double jobDistance = parseMilesDistance(result.distance());
                        if (jobDistance == -1.0) {
                            return false;
                        }
                        return jobDistance <= Double.parseDouble(finalDistance);
                    });
            assertTrue("All the job location distance should be with in the range of: " + distance, distanceMatch);
        }
        boolean employerMatch = jobResults.stream().anyMatch(result -> result.employer().toLowerCase(Locale.ROOT).contains(employer.toLowerCase(Locale.ROOT)));
        assertTrue("At least one employer should contain the word: " + employer, employerMatch);
        if (!payRange.isEmpty()) {
            double[] searchRange = parsePayRange(payRange);
            double searchLow = searchRange[0];
            double searchHigh = searchRange[1];
            boolean payRangeMatch = jobResults.stream()
                    .allMatch(result -> {
                        double[] jobResultParsedRange = parsePayRange(result.payRange());
                        double jobLow = jobResultParsedRange[0];
                        double jobHigh = jobResultParsedRange[1];
                        return Math.max(jobLow, searchLow) <= Math.min(jobHigh, searchHigh);
                    });
            assertTrue("All the pay range will be within the range of: " + payRange, payRangeMatch);
        }
    }

    @Then("I sort the search results by {string}")
    public void iSortTheSearchResultsBy(String sortBy) throws AutomationFrameworkException {
        nhsJobSearchPage.selectSortBySearch(sortBy);
    }

    @And("the search results should be sorted by newest date posted")
    public void theSearchResultsShouldBeSortedByNewestDatePosted() throws AutomationFrameworkException {
        DateTimeFormatter DATE_FULL_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
        List<String> dateStrings = nhsJobSearchPage.getJobResultDates();
        assertFalse("Job dates list should not be empty for sorting verification", dateStrings.isEmpty());
        // 2. Parse raw date strings into a list of LocalDate objects
        List<LocalDate> actualParsedDates = new ArrayList<>();
        for (String dateString : dateStrings) {
            LocalDate parsedDate = parseJobDate(dateString);
            if (parsedDate != null) {
                actualParsedDates.add(parsedDate);
            }
        }

        // Assert that we have valid dates to compare after parsing
        assertFalse("No valid dates found for sorting comparison after parsing all job results.", actualParsedDates.isEmpty());

        // 3. Verify dates are in descending order (newest first)
        for (int i = 0; i < actualParsedDates.size() - 1; i++) {
            LocalDate currentDate = actualParsedDates.get(i);
            LocalDate nextDate = actualParsedDates.get(i + 1);
            assertTrue(
                    String.format(
                            "Dates are not sorted by newest date posted. Issue at index %d: '%s' (current) vs '%s' (next)",
                            i,
                            currentDate.format(DATE_FULL_MONTH_FORMATTER),
                            nextDate.format(DATE_FULL_MONTH_FORMATTER)
                    ),
                    !currentDate.isBefore(nextDate)
            );
        }


    }

    @Then("The page should be accessibility tested")
    public void thePageShouldBeAccessibilityTested() {
        JSONObject responseJson = new AXE.Builder(BrowserConfig.driver, scriptURL).analyze();
        JSONArray violations = responseJson.getJSONArray("violations");
        if (violations.length() == 0) {
            System.out.println("No violations found");
        } else {
            assertTrue(AXE.report(violations), false);
        }
    }
}




