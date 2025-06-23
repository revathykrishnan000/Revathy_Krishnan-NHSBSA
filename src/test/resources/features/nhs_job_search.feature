Feature: NHS Job Search

  Scenario Outline: Search for jobs in the NHS  and sorts by the newest date posted

    Given I am a jobseeker on the NHS Jobs website
    When I search for jobs with job title or skills "<keyword>"
    And my location is "<location>"
    And I prefer a distance of "<distance>" from the location "<location>"
    And I click more search options link
    And the employer is "<employer>"
    And the pay range is "<pay_range>"
    And I click the Search button
    Then I should see a list of jobs matching keyword: "<keyword>",location: "<location>",distance: "<distance>",employer: "<employer>" and pay range: "<pay_range>"
    Then I sort the search results by "<sort_by>"
    And the search results should be sorted by newest date posted

    Examples:
      | keyword           | location   | distance | employer | pay_range          | sort_by              |
#      |                   |            |          |          | £40,000 to £50,000 | Date Posted (newest) |
#      | Doc               |            |          |          |                    | Date Posted (newest) |
#      | Admin             | Birmingham |          | Trust    |                    | Date Posted (newest) |
#      |                   | Manchester | +5 Miles |          |                    | Date Posted (newest) |
#      |                   |            |          | Trust    |                    | Date Posted (newest) |
#      |                   | Glasgow    |          |          |                    | Date Posted (newest) |
      | Advanced Clinical | Whitehaven | +5 Miles | Trust    |                    | Date Posted (newest) |
      |                   |            |          |          |                    | Date Posted (newest) |
      |                   |            |          |          | £10,000 to £20,000 | Date Posted (newest) |