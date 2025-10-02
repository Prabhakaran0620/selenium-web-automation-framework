package stepDefinition;

import baseClass.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import Utility.ConfigReader;


public class Hooks {
	private static Set<String> allScenarios = new HashSet<>(); // Tracks all unique scenarios
	private static Map<String, String> scenarioStatusMap = new ConcurrentHashMap<>();

    @Before(order = 0)
    public void before(Scenario scenario) {
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
	public void after(Scenario scenario) throws Exception {
		try {
			String scenarioName = scenario.getName();
			allScenarios.add(scenarioName); // Track all scenarios

			if (scenario.getStatus().name().equalsIgnoreCase("PASSED")) {
				scenarioStatusMap.put(scenarioName, "PASS");
				System.out.println(scenarioName + " test Execution Passed");
			} else {
				scenarioStatusMap.put(scenarioName, "FAIL");
				System.out.println(scenarioName + " test Execution Failed (" + scenario.getStatus().name() + ")");
				scenario.log(scenarioName + " has failed or was not fully executed");
			}
		}

		catch (Exception e) {
			System.out.println("Error in after hook: " + e.getMessage());
		}
		BaseTest.quitBrowser();
		Thread.sleep(5000);
		System.out.println("--------------- Scenario Ends and browser closed -----------------------");
	}

}
