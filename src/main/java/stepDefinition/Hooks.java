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
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.nio.file.*;
import Utility.ConfigReader;

public class Hooks {
	private static Set<String> allScenarios = new HashSet<>(); // Tracks all unique scenarios
	private static Map<String, String> scenarioStatusMap = new ConcurrentHashMap<>();
	public static ExtentReports extent;
	public static ExtentTest test;
	public static ExtentSparkReporter spark;

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
			allScenarios.add(scenarioName);

			if (scenario.getStatus().name().equalsIgnoreCase("PASSED")) {
				scenarioStatusMap.put(scenarioName, "PASS");
				System.out.println(scenarioName + " test Execution Passed");

				// You can optionally log pass to extent
				Hooks.test = Hooks.extent.createTest(scenarioName);
				Hooks.test.pass("Scenario passed ✅");

			} else {
				scenarioStatusMap.put(scenarioName, "FAIL");
				System.out.println(scenarioName + " test Execution Failed (" + scenario.getStatus().name() + ")");
				scenario.log(scenarioName + " has failed or was not fully executed");

				// Generate screenshot filename
				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String screenshotFileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

				// Take screenshot and get full path
				String screenshotPath = BaseTest.takeSnapShot(screenshotFileName);

				if (screenshotPath != null && Files.exists(Paths.get(screenshotPath))) {
					byte[] screenshotBytes = Files.readAllBytes(Paths.get(screenshotPath));

					// ✅ Attach to Cucumber
					scenario.attach(screenshotBytes, "image/png", "Failure Screenshot");

					// ✅ Attach to Extent
					String relativePath = "screenshots/" + screenshotFileName;

					// Create extent test and attach screenshot
					Hooks.test = Hooks.extent.createTest(scenarioName);
					Hooks.test.fail("Scenario failed ❌")
							  .addScreenCaptureFromPath(relativePath);

				} else {
					scenario.log("⚠️ Screenshot not available due to an internal error.");
				}
			}

		} catch (Exception e) {
			System.out.println("❌ Error in @After hook: " + e.getMessage());
			e.printStackTrace();
		}

		BaseTest.quitBrowser();
		Thread.sleep(1000);
		System.out.println("--------------- Scenario Ends and browser closed -----------------------");
	}


}

//package stepDefinition;
//
//import baseClass.BaseTest;
//import io.cucumber.java.*;
//import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.nio.file.Files;
//
//import com.aventstack.extentreports.*;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import Utility.ConfigReader;
//
//public class Hooks {
//
//    private static Set<String> allScenarios = new HashSet<>();
//    private static Map<String, String> scenarioStatusMap = new ConcurrentHashMap<>();
//    public static ExtentReports extent;
//    public static ExtentTest test;
//    public static ExtentSparkReporter spark;
//
//    /** ✅ Replace TestNG's @BeforeSuite **/
//    @BeforeAll
//    public static void setupExtentReport() {
//        spark = new ExtentSparkReporter(System.getProperty("user.dir") + "/test-output/ExtentReport.html");
//        spark.config().setDocumentTitle("Automation Report");
//        spark.config().setReportName("POM Framework Test Report");
//
//        extent = new ExtentReports();
//        extent.attachReporter(spark);
//        extent.setSystemInfo("Host Name", "Localhost");
//        extent.setSystemInfo("Environment", "QA");
//        extent.setSystemInfo("User", "Prabhakaran");
//    }
//
//    @Before(order = 0)
//    public void before(Scenario scenario) {
//        try {
//            ConfigReader.loadProperties("qa");
//            BaseTest.testcasename = scenario.getName();
//            test = extent.createTest("Scenario: " + scenario.getName()); // 💡 Create ExtentTest per scenario
//            System.out.println("---------------- Scenario Starts -----------------------");
//            System.out.println("Scenario Name ----> " + scenario.getName());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to load properties: " + e.getMessage());
//        }
//    }
//
//    @After
//    public void after(Scenario scenario) {
//        try {
//            String scenarioName = scenario.getName();
//            allScenarios.add(scenarioName);
//
//            if (scenario.getStatus().name().equalsIgnoreCase("PASSED")) {
//                test.pass("✅ Scenario Passed: " + scenarioName);
//                scenarioStatusMap.put(scenarioName, "PASS");
//            } else {
//                test.fail("❌ Scenario Failed: " + scenarioName);
//                scenarioStatusMap.put(scenarioName, "FAIL");
//
//                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String screenshotName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
//
//                String screenshotPath = BaseTest.takeSnapShot(screenshotName);
//                if (screenshotPath != null) {
//                    byte[] screenshot = Files.readAllBytes(Paths.get(screenshotPath));
//                    scenario.attach(screenshot, "image/png", "Failure Screenshot");
//                    test.addScreenCaptureFromPath(screenshotPath);
//                } else {
//                    test.warning("⚠️ Screenshot not available");
//                }
//            }
//
//        } catch (Exception e) {
//        	test.fail("Error in After Hook: " + e.getMessage());
//        }
//
//        BaseTest.quitBrowser();
//        System.out.println("--------------- Scenario Ends and browser closed -----------------------");
//    }
//
//    /** ✅ Replace TestNG's @AfterSuite **/
//    @AfterAll
//    public static void tearDownReport() {
//        extent.flush(); // 💥 Needed to write the report file
//    }
//}
//
