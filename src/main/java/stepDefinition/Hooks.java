package stepDefinition;

import baseClass.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import Utility.ConfigReader;

public class Hooks {
	private static Set<String> allScenarios = new HashSet<>();
	private static Map<String, String> scenarioStatusMap = new ConcurrentHashMap<>();
	public static ExtentReports extent;
	public static ExtentTest test;
	public static ExtentSparkReporter spark;

	@BeforeAll
	public static void setupExtentReport() {
		String reportPath = System.getProperty("user.dir") + "/test-output/SparkReport/ExtentReport.html";
		spark = new ExtentSparkReporter(reportPath);

		// Configure report appearance
		spark.config().setTheme(Theme.STANDARD);
		spark.config().setDocumentTitle("Automation Test Report");
		spark.config().setReportName("Test Execution Report");
		spark.config().setTimeStampFormat("dd-MMM-yyyy HH:mm:ss");

		extent = new ExtentReports();
		extent.attachReporter(spark);

		// Add environment details
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("Operating System", System.getProperty("os.name"));
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Java Version", System.getProperty("java.version"));

		System.out.println("✅ Extent Report initialized at: " + reportPath);
	}

	@Before(order = 0)
	public void before(Scenario scenario) {
		try {
			ConfigReader.loadProperties("qa");
			System.out.println("---------------- Scenario Starts -----------------------");
			BaseTest.testcasename = scenario.getName();
			System.out.println("Scenario Name ----> " + scenario.getName());

			// Create test in Extent Report at the start
			Hooks.test = Hooks.extent.createTest(scenario.getName());
			Hooks.test.info("Scenario: " + scenario.getName());

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

				if (Hooks.test != null) {
					Hooks.test.pass(MarkupHelper.createLabel("Scenario PASSED ✅",
							com.aventstack.extentreports.markuputils.ExtentColor.GREEN));
				}

			} else {
				scenarioStatusMap.put(scenarioName, "FAIL");
				System.out.println(scenarioName + " test Execution Failed (" + scenario.getStatus().name() + ")");
				scenario.log(scenarioName + " has failed or was not fully executed");

				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String screenshotFileName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";

				// Take screenshot and get full path
				String screenshotPath = BaseTest.takeSnapShot(screenshotFileName);

				if (screenshotPath != null && Files.exists(Paths.get(screenshotPath))) {
					byte[] screenshotBytes = Files.readAllBytes(Paths.get(screenshotPath));

					// Attach to Cucumber report
					scenario.attach(screenshotBytes, "image/png", "Failure Screenshot");

					// Calculate RELATIVE path from Extent Report to screenshot
					File reportFile = new File(
							System.getProperty("user.dir") + "/test-output/SparkReport/ExtentReport.html");
					File screenshotFile = new File(screenshotPath);

					String relativePath = getRelativePath(reportFile.getParentFile(), screenshotFile);

					System.out.println("📸 Screenshot saved at: " + screenshotPath);
					System.out.println("📊 Relative path for Extent: " + relativePath);

					// Attach to Extent Report using relative path
					if (Hooks.test != null) {
						Hooks.test.fail(MarkupHelper.createLabel("Scenario FAILED ❌",
								com.aventstack.extentreports.markuputils.ExtentColor.RED));
						Hooks.test.fail("Failure Screenshot:", com.aventstack.extentreports.MediaEntityBuilder
								.createScreenCaptureFromPath(relativePath).build());

						// Add failure details if available
						if (scenario.getStatus().toString().contains("FAILED")) {
							Hooks.test.fail("Status: " + scenario.getStatus());
						}
					}
				} else {
					scenario.log("⚠️ Screenshot not available due to an internal error.");
					if (Hooks.test != null) {
						Hooks.test.fail(MarkupHelper.createLabel("Scenario FAILED ❌ (No screenshot available)",
								com.aventstack.extentreports.markuputils.ExtentColor.RED));
					}
				}
			}

		} catch (Exception e) {
			System.out.println("❌ Error in @After hook: " + e.getMessage());
			e.printStackTrace();
		} finally {
			BaseTest.quitBrowser();
			Thread.sleep(2000);
			System.out.println("--------------- Scenario Ends and browser closed -----------------------");
		}
	}

	/**
	 * Calculate relative path from report directory to screenshot file
	 */
	private static String getRelativePath(File reportDir, File screenshotFile) {
		try {
			Path reportPath = reportDir.toPath();
			Path screenshotPath = screenshotFile.toPath();
			Path relativePath = reportPath.relativize(screenshotPath);
			return relativePath.toString().replace("\\", "/");
		} catch (Exception e) {
			// Fallback to absolute path if relative path calculation fails
			return screenshotFile.getAbsolutePath().replace("\\", "/");
		}
	}

	@AfterAll
	public static void afterExecution() throws IOException {
		if (extent != null) {

			// --- NEW: Calculate and add test summary to system info ---
			long total = allScenarios.size();
			long passed = scenarioStatusMap.values().stream().filter(s -> s.equals("PASS")).count();
			long failed = scenarioStatusMap.values().stream().filter(s -> s.equals("FAIL")).count();
			long skipped = total - (passed + failed); // Calculates SKIPPED scenarios

			extent.setSystemInfo("Total Scenarios", String.valueOf(total));
			extent.setSystemInfo("Total Passed", String.valueOf(passed));
			extent.setSystemInfo("Total Failed", String.valueOf(failed));
			extent.setSystemInfo("Total Skipped", String.valueOf(skipped));

			System.out.println("📊 Test Summary Added: Total=" + total + ", Passed=" + passed + ", Failed=" + failed
					+ ", Skipped=" + skipped);
			// -------------------------------------------------------------

			extent.flush();
			System.out.println("✅ Extent Report flushed successfully!");
		}

		// Clean unwanted test-output folders except screenshots and SparkReport
		File testOutputDir = new File(System.getProperty("user.dir") + "/test-output");
		File[] files = testOutputDir.listFiles();

		if (files != null) {
			for (File file : files) {
				if (!file.getName().equalsIgnoreCase("screenshots")
						&& !file.getName().equalsIgnoreCase("SparkReport")) {
					if (file.isDirectory()) {
						FileUtils.deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
	}
}