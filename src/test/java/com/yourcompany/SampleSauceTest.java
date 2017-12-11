package com.yourcompany;

import com.saucelabs.common.SauceOnDemandAuthentication;

import com.saucelabs.example.util.ResultReporter;

import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.example.util.ResultReporter;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;


@Listeners({SauceOnDemandTestListener.class})
public class SampleSauceTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

   public String sauce_username = System.getenv("SAUCE_USERNAME");
   public String sauce_accesskey = System.getenv("SAUCE_ACCESS_KEY");
   public String testobject_apikey = System.getenv("TO_APIKEY");
    
    
//    public static String testing_location = "desktop";
  public static String testing_location = "emulator";
//    public static String testing_location = "mobile";

    private ResultReporter reporter;
    /**
     * Constructs a {@link SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(sauce_username, sauce_accesskey);

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return
     * @throws JSONException 
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        
        
   
        if (testing_location == "desktop") {
            Object browsers = new Object[][] {
            //return new Object[][]{

             // Windows
            
                new Object[]{"chrome", "latest", "Windows 10", "null"},
            new Object[]{"chrome", "latest", "Windows 7", "nunll"},
            new Object[]{"chrome", "latest", "Windows 8.1", "null"}
//            new Object[]{"chrome", "57", "Windows 10"},
//            new Object[]{"chrome", "57", "Windows 7"},
//            new Object[]{"chrome", "57", "Windows 8.1"},
//            new Object[]{"chrome", "58", "Windows 10"},
//            new Object[]{"chrome", "58", "Windows 7"},
//            new Object[]{"chrome", "58", "Windows 8.1"},
//            new Object[]{"chrome", "59", "Windows 10"},
//            new Object[]{"chrome", "59", "Windows 7"},
//            new Object[]{"chrome", "59", "Windows 8.1"},
//            new Object[]{"internet explorer", "11", "Windows 8.1"},
//            new Object[]{"internet explorer", "10.0", "Windows 8"},
//            new Object[]{"internet explorer", "8", "Windows 7"},
//            new Object[]{"firefox", "latest", "Windows 7"},
//            new Object[]{"firefox", "latest", "Windows 8.1"},
//            new Object[]{"firefox", "latest", "Windows 10"},
//            new Object[]{"firefox", "52", "Windows 7"},
//            new Object[]{"firefox", "52", "Windows 8.1"},
//            new Object[]{"firefox", "52", "Windows 10"},
//            new Object[]{"firefox", "53", "Windows 7"},
//            new Object[]{"firefox", "53", "Windows 8.1"},
//            new Object[]{"firefox", "53", "Windows 10"},
    
            // Mac
            //new Object[]{"firefox", "48", "macOS 10.12"},
            //new Object[]{"safari", "9", "OS X 10.11"},
            //new Object[]{"chrome", "50", "OS X 10.10"},
            //new Object[]{"firefox", "45", "OS X 10.9"},
            //new Object[]{"safari", "6.0", "OS X 10.8"},
            
            // Linux
            //new Object[]{"firefox", "45", "Linux"},
            //new Object[]{"chrome", "48", "Linux"}
        
            };
            return (Object[][]) browsers;
 
        } else if (testing_location == "emulator"){
            Object browsers = new Object[][] {
                //return new Object[][]{
                
                    new Object[]{"safari", "11.0", "iOS", "iPhone 7 Plus Simulator"},
                    new Object[]{"chrome", "7.0", "Android", "Android GoogleAPI Emulator"},
                    new Object[]{"safari", "11.0", "iOS", "iPhone 8 Simulator"}
//                new Object[]{"chrome", "latest", "Windows 7"},
//                new Object[]{"chrome", "latest", "Windows 8.1"}
            };
            return (Object[][]) browsers;
        } else { //running on real devices
            Object devices = new Object[][] {
                //return new Object[][]{
                
            new Object[]{"null", "10", "iOS", "iPhone SE"},
            new Object[]{"null", "6", "Android", "Samsung Galaxy S6"}
//                new Object[]{"chrome", "latest", "Windows 7"},
//                new Object[]{"chrome", "latest", "Windows 8.1"}
            };
            return (Object[][]) devices;
        }
        
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String browser, String version, String os, String device, String methodName) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();


        
        if (testing_location != "mobile") {
            capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
            capabilities.setCapability(CapabilityType.VERSION, version);
            capabilities.setCapability(CapabilityType.PLATFORM, os);
            
            
            
        } 
        
        if (testing_location == "emulator") {
                capabilities.setCapability("deviceName", device);
        }
        
        if (testing_location == "mobile") {
            capabilities.setCapability("testobject_api_key", testobject_apikey);
            capabilities.setCapability("deviceName", device);
            capabilities.setCapability("platformVersion", version);
            capabilities.setCapability("platformName", os);
            capabilities.setCapability("name",  methodName);
            capabilities.setCapability("appiumVersion", "1.7.1");
            
        }
        
        //Sends test name as a desired capability to update the Sauce Labs dash board
        String jobName;
        if (testing_location == "desktop") {
                jobName = methodName + '_' + os + '_' + browser + '_' + version;
        } else if (testing_location == "emulator") {
                jobName = methodName + '_' + os + '_' + device + '_' + browser + '_' + version;
        } else {
                jobName = methodName + '_' + os + '_' + version;
        }
        capabilities.setCapability("name", jobName);

        //Local Driver

        // WebDriver driver = new FireFoxDriver();
        
        //Creates Selenium Driver
        if (testing_location != "mobile") {
            webDriver.set(new RemoteWebDriver(
                    new URL("https://" + sauce_username + ":" + sauce_accesskey + "@ondemand.saucelabs.com:443/wd/hub"),
                    capabilities));
        } else {
            webDriver.set(new RemoteWebDriver(
                    new URL("http://us1.appium.testobject.com/wd/hub"),
                    capabilities));
        }
        
      //Keeps track of the unique Selenium session ID used to identify jobs on Sauce Labs
        if (testing_location != "mobile") {
            String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
            sessionId.set(id);
            
            //For CI plugins
            String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName);
            System.out.println(message);
        }
        return webDriver.get();
    }

//    @AfterMethod
//    public void tearDown() throws Exception {
//        webDriver.get().quit();
//    }

    /**
     * Runs a simple test navigating on the Mayo Clinic website
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @param Method Represents the method, used for getting the name of the test/method
     * @throws Exception if an error occurs during the running of the test
     */
    

    @Test(dataProvider = "hardCodedBrowsers")
    public void mayoSimpleNavTest(String browser, String version, String os, String device, Method method) throws Exception {
        WebDriver driver = createDriver(browser, version, os, device, method.getName());
        WebDriverWait wait = new WebDriverWait(driver, 15);
        
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        
        driver.get("http://mayoclinic.org");
        
//        WebElement hoverElement = driver.findElement(By.id("et_globalNavigation_E098AB76"));
//        Actions builder = new Actions(driver);
//        
//        builder.moveToElement(hoverElement).build().perform();
//        By locator = By.id("et_globalNavigation_EAE6F61B");
//        driver.click(locator);
        
        if (testing_location == "desktop") {
            Actions actions = new Actions(driver);
            WebElement mainMenu = driver.findElement(By.id("et_globalNavigation_E098AB76"));
            actions.moveToElement(mainMenu);
    
            WebElement subMenu = driver.findElement(By.id("et_globalNavigation_EAE6F61B"));
            actions.moveToElement(subMenu);
            actions.click().build().perform();
        } else {
                driver.findElement(By.xpath("//*[@id=\"mobilenav\"]/div[1]/ul/li[1]/a")).click();
                
                try{
                   Thread.sleep(10000);
                  }catch (InterruptedException ie1) {
                    //ie1.printStackTrace();
                  } 
                
                driver.findElement(By.id("et_globalNavigation_EAE6F61B")).click();

        }
        
        try{
               Thread.sleep(10000);
              }catch (InterruptedException ie1) {
                //ie1.printStackTrace();
              } 
        if (testing_location != "mobile") {
                assertEquals(driver.getTitle(), "Healthy Lifestyle - Healthy Lifestyle - Mayo Clinic");
        }
    }
    /**
     * @return the {@link WebDriver} for the current thread
     */
    public RemoteWebDriver getWebDriver() {
        System.out.println("WebDriver" + webDriver.get());
        return (RemoteWebDriver) webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
    if (testing_location == "mobile") {
            RemoteWebDriver driver = getWebDriver();
            reporter = new ResultReporter();
            boolean success = result.isSuccess();
            String sessionId = driver.getSessionId().toString();
    
            reporter.saveTestStatus(sessionId, success);
            driver.quit();
        } else {
            webDriver.get().quit();
        }
    }
    /**
     *
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return authentication;
    }
}