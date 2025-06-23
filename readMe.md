<!-- TOC -->
* [**NHS Jobs Automation**](#nhs-jobs-automation)
  * [✨ Features](#-features)
  * [🚀 Getting Started](#-getting-started)
    * [Prerequisites](#prerequisites)
    * [📦 Build Commands](#-build-commands)
  * [📊 Results and Reporting](#-results-and-reporting)
<!-- TOC -->

# **NHS Jobs Automation**

This tool automates testing for the NHS job search, including its functionality and accessibility.

## ✨ Features

* Test Cases via Feature Files: Easily readable and maintainable test scenarios defined in Gherkin (.feature) files.
* Step Definitions: Clear and modular Java code mapping directly to feature file steps.
* Test runner: Dedicated class for flexible execution of specific tests or entire suites.
* Browser Agnostic: Configurable browser execution (e.g., Chrome,Firefox).
* Detailed Reporting: Generates comprehensive Cucumber reports with test pass/fail status and failure screenshots.

## 🚀 Getting Started

Follow these instructions to set up and run the automation tests on your local machine.

###  Prerequisites

Before you begin, ensure you have the following installed:

*   Java Development Kit (JDK): Version 11 or higher.
* Download JDK
* Apache Maven: Version 3.6.0 or higher.
* Download Maven
* Google Chrome Browser: The automation is configured to run on Chrome by default.
* Download Chrome
* ChromeDriver:The tool automatically downloads the necessary browser drivers

### 📦 Build Commands

To compile the project and create the executable JAR file:

* Clean and Install (skipping tests):

mvn clean install -Dmaven.test.skip=true
This command will build the project and package it into an executable JAR (e.g., NHSJobsAutomation-1.0.jar) in the target/ directory, without running the tests during the build process.

* 🏃 Running Tests (Development Workflow)

Once the JAR is built, you can execute the tests from your command line.

* Prepare the Execution Environment:

Create a dedicated directory for running the tests (e.g., run_env).
Copy the generated JAR file (NHSJobsAutomation-1.0.jar) from your project's target/ directory into your run_env folder.
Copy the entire features/ folder (typically found in src/test/resources/) into the run_env folder.
Copy the entire properties/ folder (from src/test/resources/) into the run_env folder.

* Execute Tests:

Navigate to your run_env directory in your terminal or command prompt and run the following command:

java -DbrowserName=Chrome -jar NHSJobsAutomation-1.0.jar 
-DbrowserName=Chrome: Specifies that tests should run on the Chrome browser. (You might extend this to support other browsers if implemented).
-DbrowserName=Firefox: Specifies that tests should run on the Firefox browser. (You might extend this to support other browsers if implemented).
-DbrowserName=HeadlessFirefox: Specifies that tests should run on the HeadlessFirefox browser. (You might extend this to support other browsers if implemented).
-DbrowserName=HeadlessChrome: Specifies that tests should run on the HeadlessChrome browser. (You might extend this to support other browsers if implemented).
## 📊 Results and Reporting

After the test execution completes, all generated reports will be found in the following directory:

Reports Location: cucumber-reports/

This directory will contain detailed reports on the test execution, including:

Summary of passed, failed, and skipped tests.
Timestamps of execution.
Screenshots for Failed Test Cases: These are automatically captured and linked in the reports to help with quick debugging.

