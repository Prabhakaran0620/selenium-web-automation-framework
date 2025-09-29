package stepDefinition;

import baseClass.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import Utility.ConfigReader;


public class Hooks {
	
    @Before(order = 0)
    public void setup(Scenario scenario) {
        try {
            ConfigReader.loadProperties("qa");
    		System.out.println("---------------- Scenario Starts -----------------------");
    		BaseTest.testcasename = scenario.getName();
    		System.out.println("Scenario Name ----> " + scenario.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load properties: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        BaseTest.quitBrowser();
    }

}
