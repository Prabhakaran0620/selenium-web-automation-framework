@LoginTestRun
Feature: Login Scenario

Scenario: TC01_Login_Verify login functionality with valid user credentials
    Given Open "baseURL" website in "chrome" browser
    When Enter value "config:practiceUsername" into "Username"
    And Enter value "config:practiceUserPassword" into "Password"
    And Hover and click on "loginButton"
    And Wait for element "practiceHomePage" to load
    Then Check "practiceHomePage" is displayed
    And Close browser