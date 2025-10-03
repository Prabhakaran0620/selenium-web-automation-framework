package stepDefinition;

import baseClass.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.*;
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

                // Generate unique screenshot name
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String screenshotName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp  + ".png";

                // Take screenshot and attach to Cucumber report
                String screenshotPath = BaseTest.takeSnapShot(screenshotName);
                if (screenshotPath != null) {
                    byte[] screenshot = Files.readAllBytes(Paths.get(screenshotPath));
                    scenario.attach(screenshot, "image/png", "Failure Screenshot");
                } else {
                    scenario.log("⚠️ Screenshot not available due to an internal error.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error in after hook: " + e.getMessage());
        }

        // Quit browser
        BaseTest.quitBrowser();
        Thread.sleep(2000);
        System.out.println("--------------- Scenario Ends and browser closed -----------------------");
    }
    
}
