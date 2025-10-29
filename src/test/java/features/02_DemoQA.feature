@DemoTestRun
Feature: Demo QA Components 

Scenario: TC01_DemoQA_Verify Text Box Components in DemoQA web application
    Given Open "demoQa" website in "chrome" browser
    When Scroll down to the element of "elements"
    And Hover and click on "elements"
    And Wait for element "demoQaHomePage" to load
    Then Check "demoQaHomePage" is displayed
    And Hover and click on "textBox"
    And Enter value "config:demoQA_fullName" into "fullName_textBox"
    And Enter value "config:demoQA_Email" into "email_textBox"
    And Enter value "config:demoQa_Address" into "currentAddress_textBox"
    And Enter value "config:demoQa_Address" into "permananentAddress_textBox"
    And Hover and click on "textBox_Submit"
    Then Assert element with xpath "textBoxOutput" has contains "test"
    And Close browser

Scenario: TC02_DemoQA_Verify Checknox Box Components in DemoQA web application
    Given Open "demoQa" website in "chrome" browser
    When Scroll down to the element of "elements"
    And Hover and click on "elements"
    And Wait for element "demoQaHomePage" to load
    Then Check "demoQaHomePage" is displayed    
    And Hover and click on "checkbox"
    Then Wait for element "checkBoxHomePage" to load
    And Hover and click on "home_checkBox"
    Then Check "home_checkBoxConfirmation" is displayed 
    And Close browser

    