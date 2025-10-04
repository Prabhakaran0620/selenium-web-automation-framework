package engine;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/features",
    glue = {"stepDefinition"},
    plugin = {
//    		"pretty", 
    		"summary", 
            "json:target/cucumber-report/cucumber.json",
            "html:target/cucumber-report/cucumber.html",
            "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    		},
    monochrome = true,
    tags = "@LoginTestRun"
)


public class Runner {
	

}
