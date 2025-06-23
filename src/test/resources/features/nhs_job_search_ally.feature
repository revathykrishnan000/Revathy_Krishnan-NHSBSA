Feature: NHS Job Search

  Scenario: Search for jobs in the NHS  and check the accessibility violations in the page

    Given I am a jobseeker on the NHS Jobs website
    When I click the Search button
    Then The page should be accessibility tested