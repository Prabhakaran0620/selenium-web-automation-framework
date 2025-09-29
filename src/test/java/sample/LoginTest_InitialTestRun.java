package sample;

import org.testng.annotations.Test;

import baseClass.*;

public class LoginTest_InitialTestRun extends BaseTest {

    @Test
    public void sampleTest() {
//    	Automation Demo site URL
        driver.get("https://demo.automationtesting.in/Register.html");
        System.out.println("Title is: " + driver.getTitle());
    }
}


