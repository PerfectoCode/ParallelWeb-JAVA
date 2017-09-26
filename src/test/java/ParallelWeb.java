
import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ParallelWeb {
    RemoteWebDriver driver;
    ReportiumClient reportiumClient;

    // Create Remote WebDriver based on testng.xml configuration
    @Parameters({ "platformName", "platformVersion", "browserName", "browserVersion", "screenResolution", "location" })
    @BeforeTest
    public void beforeTest(String platformName, String platformVersion, String browserName, String browserVersion, String screenResolution, @Optional String location) throws MalformedURLException {
        driver = Utils.getRemoteWebDriver(platformName, platformVersion, browserName, browserVersion, screenResolution, location);
        // Reporting client. For more details, see http://developers.perfectomobile.com/display/PD/Reporting
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withProject(new Project("Sample Script", "1.0"))
                .withJob(new Job("Sample Job", 45))
                .withContextTags("Java")
                .withWebDriver(driver)
                .build();
        reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
    }

    // Test Method - Navigate to way2automation.com and fill the registration form
    @Test
    public void way2automation(){
        try{
            reportiumClient.testStart("Way2Automation Flow", new TestContext("Sample", "Native"));

            reportiumClient.stepStart("Navigate to site");
            driver.get("http://way2automation.com/way2auto_jquery/index.php");
            delay();
            reportiumClient.stepEnd();

            reportiumClient.stepStart("Opening signin window");
            //try{
            //    clickText("Signin", 60);
            //} catch (Exception ex){
                driver.findElementByXPath("(//*[@id=\"load_form\"]/div/div[1]/p/a)[2]").click();
            //}
            delay();
            reportiumClient.stepEnd();

            reportiumClient.stepStart("Signing in");
            login("liron","123456");
            delay();
            reportiumClient.stepEnd();

            ///
            reportiumClient.stepStart("Going to registration");
            driver.findElementByXPath("//*[@id=\"wrapper\"]/div[2]/div[2]/div[5]/ul/li/a/h2").click();
            delay();
            driver.findElement(By.id("register_form")).isDisplayed();
            reportiumClient.stepEnd();

            reportiumClient.stepStart("Test Registration");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[1]/p[1]/input").sendKeys("first name");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[1]/p[2]/input").sendKeys("last name");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[2]/div/label[1]/input").click();
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[3]/div/label[1]/input").click();
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[3]/div/label[2]/input").click();
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[6]/input").sendKeys("1234567890"); //Phone Number
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[7]/input").sendKeys("username");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[8]/input").sendKeys("Email@gmail.com");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[11]/input").sendKeys("Password");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[12]/input").sendKeys("Password");
            driver.findElementByXPath("//*[@id=\"register_form\"]/fieldset[13]/input").click(); //Click Submit
            delay();
            reportiumClient.stepEnd();

            ///

            reportiumClient.testStop(TestResultFactory.createSuccess());

        } catch (InterruptedException e) {
            reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Report URL:" + reportiumClient.getReportUrl());
                System.out.println(driver.getCapabilities().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Way2automation flow ended");
    }

    public void delay() throws InterruptedException { TimeUnit.SECONDS.sleep(10);}

    public void clickText(String text, int threshold){
        Map<String, Object> params = new HashMap<>();
        params.put("content", text);
        params.put("threshold", threshold);
        params.put("scrolling", "scroll");
        driver.executeScript("mobile:text:select", params);
    }

    public void login(String username, String password) throws InterruptedException {
        driver.findElementByXPath("(//*[@name='username'])[2]").sendKeys(username);
        driver.findElementByXPath("(//*[@name='password'])[2]").sendKeys(password);
        delay();
        //try{
        //    clickText("SUBMIT", 60);
        //} catch (Exception ex){
            driver.findElementByXPath("(//*[@id=\"load_form\"]/div/div[2]/input)[2]").click();
        //}
    }

    @AfterTest
    public void afterTest() throws IOException {
        driver.close();
        driver.quit();
    }
}
