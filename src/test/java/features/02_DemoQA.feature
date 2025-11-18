@DemoTestRun
Feature: Demo QA Components 

# TextBox
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

# Checkbox
Scenario: TC02_DemoQA_Verify Checkbox Box Components base functionality in DemoQA web application
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

Scenario: TC03_DemoQA_Verify Checkbox Box expand all and collapse all features in DemoQA web application
    Given Open "demoQa" website in "chrome" browser
    When Scroll down to the element of "elements"
    And Hover and click on "elements"
    And Wait for element "demoQaHomePage" to load
    Then Check "demoQaHomePage" is displayed    
    And Hover and click on "checkbox"
    Then Wait for element "checkBoxHomePage" to load
    And Hover and click on "home_toggle"
    And Hover and click on "documents_toggle"
    And Hover and click on "office_checkBox"
    Then Check "office_checkBoxConfirmation" is displayed 
    And Hover and click on "office_checkBox"
    And Hover and click on "office_toggle"
    And Hover and click on "private_checkBox"
    And Hover and click on "private_checkBoxConfirmation"    
    And Close browser
    
# RadioButton
Scenario: TC04_DemoQA_Verify the Radio button Components base functionality in DemoQA web application
    Given Open "demoQa" website in "chrome" browser
    When Scroll down to the element of "elements"
    And Hover and click on "elements"
    And Wait for element "demoQaHomePage" to load
    Then Check "demoQaHomePage" is displayed 
    And Hover and click on "radioButton"
    Then Check "radioButtonHomePage" is displayed
    And Hover and click on "yesRadioButton"
    Then Check "yes_confirmation" is displayed
    And Hover and click on "impressiveRadioButton"
    Then Check "impressive_confirmation" is displayed
    And Close browser

    

    
    