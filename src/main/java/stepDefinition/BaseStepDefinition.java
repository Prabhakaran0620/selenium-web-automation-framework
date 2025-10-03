package stepDefinition;

import baseClass.BaseTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import Utility.ConfigReader;

public class BaseStepDefinition extends BaseTest {

    @Given("Open {string} website in {string} browser")
    public void open_website_with_browser(String urlKey, String browser) {
        ConfigReader.loadProperties("qa");
        String url = ConfigReader.getProperty(urlKey);
        System.out.println("Launching URL: " + url); 

        // Launch browser and navigate
        launchBrowser(browser);
        openUrl(url);
		System.out.println("  *****  Browser Launched  *****");

    }

    @Then("Enter value {string} into {string}")
    public void enter_text_textfield(String value, String field) throws Throwable {
        String finalValue;

        if (value.startsWith("config:")) {
            String configKey = value.replace("config:", "");
            finalValue = ConfigReader.getProperty(configKey);
        } else {
            finalValue = value;
        }

        String locator = ConfigReader.getProperty(field);
        if (locator == null) {
            throw new RuntimeException("Locator not found for field: " + field);
        }

        enterText(locatorstowebelement(locator), finalValue);
        System.out.println("  *****  Value Entered  *****");
    }

	@Then("Hover and click on {string}")
	public void hover_click_field(String field) throws InterruptedException {
		hoverAndClickElement(locatorstowebelement(ConfigReader.getProperty(field)));
		System.out.println("  *****  Hover on element and click verified  *****");
	}

	@Then("Wait for element {string} to load")
	public void fluent_wait(String field) {
		locatorstowebelement(ConfigReader.getProperty(field));
		System.out.println("  *****  Fluent Wait seconds verified  *****");

	}

	@Then("Check {string} is displayed")
	public void assert_element_is_displayed(String field) throws Throwable {
		assertElementDisplayed(locatorstowebelement(ConfigReader.getProperty(field)));
		System.out.println("  *****  Element Displayed is Verified  *****");
	}

	@Then("Close browser")
	public void close_the_browser() {
		quitBrowser();
		System.out.println("  *****  Browser closed  *****");
	}
	
    @Then("Wait for {string} seconds")
    public void wait_for_seconds(String seconds) throws Throwable {
        waitForNSec(Integer.parseInt(seconds));
		System.out.println("  *****  " + seconds + " Sec Wait Completed  *****");
    }

}
