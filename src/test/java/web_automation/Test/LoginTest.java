package web_automation.Test;

import web_automation.Base.*;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void sampleTest() {
//    	Automation Demo site URL
        driver.get("https://demo.automationtesting.in/Register.html");
        System.out.println("Title is: " + driver.getTitle());
    }
}


