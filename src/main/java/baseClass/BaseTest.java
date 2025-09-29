package baseClass;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import Utility.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

	public static WebDriver driver;
	public static final BaseTest ZERO = new BaseTest();
	public static String environment;
	public static String name_field = null;
    public static String testcasename = null;


	public static WebDriver getdriver() {
	    return driver;
	}
    
    public void launchBrowser(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;

            case "safari":
                // SafariDriver does not require WebDriverManager
                // Only works on macOS with Safari's 'Allow Remote Automation' enabled
                driver = new SafariDriver();
                break;

            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }

        driver.manage().window().maximize();
    }
    
    public void openUrl(String url) {
        driver.get(url);
    }

    public static void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

	public static Duration durationInSeconds(long seconds) {
		return Duration.ofSeconds(seconds);
	}

	public static void waitForElement(WebElement element) {
		WebDriverWait wait = new WebDriverWait(getdriver(), durationInSeconds(60));
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	public static void click(WebElement element) throws InterruptedException {
		waitForElement(element);
		element.click();
	}

	public static WebElement locatorstowebelement(String locators) {
		return fluentWait(locators, Integer.parseInt(ConfigReader.getProperty("PollingTime")));
	}

	public static boolean isElementInsideFrame(String locator) {
	    if (getdriver() == null) {
	        throw new IllegalStateException("WebDriver is not initialized. Make sure launchBrowser() is called first.");
	    }

	    List<WebElement> frames = getdriver().findElements(By.tagName("iframe"));
	    for (WebElement frame : frames) {
	        try {
	            getdriver().switchTo().frame(frame);
	            if (!getdriver().findElements(By.xpath(locator)).isEmpty()) {
	                getdriver().switchTo().defaultContent();
	                return true;
	            }
	            getdriver().switchTo().defaultContent();
	        } catch (Exception e) {
	            getdriver().switchTo().defaultContent();
	        }
	    }
	    return false;
	}

	public static void switchToFrameContainingElement(String locator) {
		List<WebElement> frames = getdriver().findElements(By.tagName("iframe"));
		for (WebElement frame : frames) {
			try {
				getdriver().switchTo().frame(frame);
				if (!getdriver().findElements(By.xpath(locator)).isEmpty()) {
					return;
				}
				getdriver().switchTo().defaultContent();
			} catch (Exception e) {
				getdriver().switchTo().defaultContent();
			}
		}
	}

	public static WebElement fluentWait(final String awaitedElement, int waitTimeInSeconds) {
	    if (awaitedElement == null || awaitedElement.trim().isEmpty()) {
	        throw new IllegalArgumentException("Cannot wait for element: XPath is null or empty");
	    }
	    Wait<WebDriver> wait = new FluentWait<>(getdriver())
	            .withTimeout(durationInSeconds(waitTimeInSeconds))
	            .pollingEvery(durationInSeconds(10))
	            .ignoring(NoSuchElementException.class)
	            .ignoring(TimeoutException.class)
	            .ignoring(IndexOutOfBoundsException.class)
	            .ignoring(StaleElementReferenceException.class);

	    return wait.until(new Function<WebDriver, WebElement>() {
	        @Override
	        public WebElement apply(WebDriver driver) {
	            for (int i = 0; i < 10; i++) {
	                try {
	                    if (isElementInsideFrame(awaitedElement)) {
	                        switchToFrameContainingElement(awaitedElement);
	                    }

	                    WebElement element = getdriver().findElement(By.xpath(awaitedElement));
	                    if (element.isDisplayed() && element.isEnabled()) {
	                        return element;
	                    }
	                } catch (StaleElementReferenceException e) {
	                    System.out.println("Stale element detected. Retrying... Attempt: " + (i + 1));
	                }
	            }
	            return null;
	        }
	    });
	}

	public static void enterText(WebElement element, String text) throws InterruptedException {
		waitForElement(element);
		element.sendKeys(Keys.CONTROL + "a");
		element.sendKeys(Keys.DELETE);
		Thread.sleep(2000);
		element.sendKeys(text);
		name_field = text;
	}

	public static void hoverAndClickElement(WebElement element) throws InterruptedException {
		waitForElement(element);
		Actions actions = new Actions(getdriver());
		actions.moveToElement(element).click().perform();
	}

	public static void assertElementDisplayed(WebElement element) throws InterruptedException {
		waitForElement(element);
		Assert.assertTrue(element.isDisplayed());
		System.out.println("element displayed is:" + element.getText());
	}

}
