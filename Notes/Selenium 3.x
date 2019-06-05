Selenium 3.x


WebDriver (I) extends SearchContext (I which has findElements and findElement)
RWD implements WD , TS, JSE etc...
CD, FD, IED.... extends RWD


WebElement(I) extends SC, TS
RemoteWebElement implements WE, TS, ...

By - abstract class with static methods , id, linkText, name etc...


webdriver and RC overcomes Selenium1.0's restriction of same origin policy - it tricks the browser tothink that the request is coming from the same domain as the application url (and not from localhost). 


To get started with selenium, you need:
selenium java bindings - zip
chrome driver jar - http://chromedriver.chromium.org/downloads/version-selection
standalone server jar is needed only if running using remote selenium webdriver to run tests on a separate machine - Remote sel WD consists of client and server, client is your tests and server is an applet that runs on J2EE server. the remote standalone server jar is installed on the same machine where the browser is.
On local, the driver instance has a HTTP server which accepts the request from client(tests) and sends it to browser configured to be run.

The below is all API related to using selenium with java nad selenium ChromeDriver classes.

 <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>3.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-chrome-driver</artifactId>
            <version>3.4.0</version>
        </dependency>

The actual driver exe itself has to be downloaded separately - http://chromedriver.chromium.org/downloads/version-selection.
This exe is the server which takes th calls from the chrome driver commands from test code and talks to the browser using the JSon wire protocol. 


From selenium 3.6 version, DesiredCapabilities is deprecated(in Java, its still there is Ruby and Python) and replaced with ChromeOptions (DesiredCapabilties can still be used in conjuction with ChromeOptions if need be)
https://sites.google.com/a/chromium.org/chromedriver/capabilities
ChromeOptions - https://www.youtube.com/watch?v=Z3u6EfZKTFM

How webdriver works/JSOn Wire Protocol  - https://jobs.zalando.com/tech/blog/selenium-webdriver-explained/?gh_src=4n3gxh1
https://sqa.stackexchange.com/questions/28358/how-does-chromedriver-exe-work-on-a-core-and-fundamental-level
https://www.youtube.com/watch?v=FkHTgJkqAFo
Selenium WD - has APIs
Chrome/Gecko/Edge drivers - server which controls browsers via native calls.  They are developed by the corresponding teams working along with Selenium team and they implement WebDriver Interface
Selenium Java Client/WD API -- JSON Wire Protocol - SERVER (Chrome/Gecko/Edge) -- native binding language - BROWSER
The Driver exe is the server. The version of drivr used depends on the version of chrome on your machine. if your chrome version is 72 and you use driver for Chrome 74, then you get RTE whicle trying to invoke the driver - Caused by: org.openqa.selenium.SessionNotCreatedException: session not created: This version of ChromeDriver only supports Chrome version 74


get() and navigate()
--------------------
navigate - navigates and has move back and forward apis, get - refreshes the page with the url.
https://www.quora.com/In-Selenium-what-is-the-difference-between-get-and-navigate-to-methods

Mouse actions 
-------------
Actions action = new Actions(driver);
action.moveToElement(element).perform();

//closes a particular window
String currentWindowHandle = driver.getWindowHandle();
Set<String> handles = driver.getWindowHandles();
Iterator<String> it = handles.iterator();
while(it.hasNext()){
String handle = it.next();
driver.switchTo().window(handle);
if(driver.getCurrentUrl().startsWith(""))
driver.close();
driver.switchTo().window(currentWindowHandle);  
}


//to switch to iframe
WebElement iframe = driver.findElelement(By.xpath(""));
driver.switchTo().iframe(iframe);

//JSExecute to scroll ot bottom of page
JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
jsExecutor.execute("window.scrollTo(0,document.body.scrollHeight)") ;

//Taking screenshot
 if(!(driver instanceof HtmlUnitDriver)) --> nice to do this scheck as htmlunitdriver doesnt support screenshots
File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

JavascriptExecutor is I with only 2 abstract methods - excuteScript and excuteAsynchScript
TakesScreenshot isan I with only 1 above abstract method.
RemoteWebDriver implements both of these Is and so you are upcasting driver to that type and then calling its methods.

Stale Element Exception - The stale element reference error is a WebDriver error that occurs because the referenced web element is no longer attached to the DOM.
This happens either because the element is deleted from DOM or because the element is no longer attched to the DOM. To handle it use try catch to handle exception or use wait.until(...)


/- absolute path , from root html node
// - relative path , from any node between html root and target


Selenium Soft and Hard Assert.
----------------------------
Regular Assert - Hard, any statements after Assert fails and execution continues with next @Test. Failure throws an AssertionError exception which needs to be handled in try catch.
The catch prints the AssertionError stacktrace and continues to next @Test but passes this test which should have failed. 
https://blogs.perficient.com/2016/01/13/hard-and-soft-assertions-in-selenium/
Soft Assert - Execution continues even if Assert fails, but test still passes. To overcome this call assertAll() at the end and if any assertionFails, then this fails the test.
Verify - execution continues even if verify fails


PageFactory.initElements(driver, this/ClassName.class); -- This initialises the elements  when the class is initialised, but doenot instantiate it, they are instantiated only when an action is performed, emaning driver looks for it only when an action is done. THis is called lazy initialisation. Can be defined in construtor of the class or called from outside when needed
https://github.com/SeleniumHQ/selenium/wiki/PageFactory
You specifically donot have to have to have the annotation in @FindBy with find strategy, WD defaults to first by id if not name of the element.
Normally everytime you do an action on element, driver goes and looksup the element in the DOM, if you donot want to do that use @CacheLookup, but this can cause StaleElementException.
https://www.seleniumeasy.com/selenium-tutorials/page-factory-pattern-in-selenium-webdriver

Advs: 
Lazy initialisation, 
neater and maintable code, 
less verbose - there is not need to write driver.findElement(By.id("")).click() ;you just say eleName.click(); once initialised.

Otherwise, without PageFactory, element is instantiated when its found ,is when doing driver.findElement();

Headless Browser - https://saucelabs.com/blog/headless-browser-testing-101

To check if testresult of the run is Failed, 
public void onTestFailure(ITestResult tr){
 if(tr.getStartMillis()==ITestResult.FAILURE) {
            //code for screenshot;
        }
 }


To deal with Hidden elements - use JavascriptExecutor

CI
---
mvn test




Debugging js in developer console - Chrome
------------------------------------------
If you want to see the js source files and debug on the js file while loading the page, in dev console - Sources - navigate to the js file ,locate the line of code , click on the line number to add a break point and then reload the page, the page reloads to the point of break point and then step over and step into from the right side panel can be used to navigate.


TestNG
------
Order of execution of @BeforeSuite, @+BeforeTest, @BeforeClass, @BeforeMethod - https://www.tutorialspoint.com/testng/testng_execution_procedure.htm
@BeforeMethod, @AfterMethod - is executed for every test method, all others are executed only once in the order - @BeforeSuite, @BeforeTest, @BeforeClass
@BeforeTest and @BeforeClass are very similar except that @BeforeTest runs before any test methods in the class mentioned in <test> tag gets executed.
@Parameters can be used to pass parmeters from testng xml file to the test methods.
Parameters can be defined at suite level or at test level in the xml, parameter at SUite level will be passed to every test in this suite
@Factory - used when test methods inside a class needs to be executed multiple times, the instance of class can be returned from the Factory method. Any parameter to be passed to the test method can be set at instance level in the class and you need to have a constructor which accepts the parameter. So, the whole class is instantiated as many times with different params passed from the Factory method. Unlike DataProvider where only a particulr test is executed multiple times.
https://howtodoinjava.com/testng/testng-difference-between-factory-and-dataprovider/

To implement your own Listener class, either extend TesListenerAdapter or implement ITestListener


Postman
-------
You can control the flow of execution in postman using 'postman.setNextRequest("{name/id of next request to be run within the same collection}");'    - scope is collection and this can be used to run a collection in postman or in newman.
Variables in postman can have scopes of Environment or Global. Global variables are available across different environments.


Cucumber
--------
Runner and Step Definitions can be created under separate packages under src/test/java
Feature files can be created under a different folder under src/test/resource
The step definition classes are recreated for every scenario, so if you want to share values between steps, then always use instance variables in the step defintion class and not static as then it would get leaked between steps, unless it needs to be shared between tests.

keywords
Runner class - using Junit
@RunWith(Cucumber.class)
@CucumberOptions(
strict = true, -- if any umiplemented methods then fail the test)
features = "path to feature files",
tags=({,}),
plugin={''}, -- reporting options
monochrome=true, --- enhance readability of report
glue={''}, -- path to step definition classes
dryRun=true -- will not fail if any unimplemented methods
)
public class Runner(){
	//is empty
}


Runner class - using TestNG - this gives reports under default TestNG reports - 'test-output' folder

@CucumberOptions(
strict = true, -- if any umiplemented methods then fail the test)
features = "path to feature files",
tags=({,}),
plugin={''}, -- reporting options
monochrome=true, --- enhance readability of report
glue={''}, -- path to step definition classes
dryRun=true -- will not fail if any unimplemented methods
)
public class Runner extends AbstractTestNGcucumberTests(){
	//is empty
}



scenario with data table - https://www.youtube.com/watch?v=N_XfFYPQnhQ&list=PLmjcJNp9bQu-rWVEdLotVdYoWsmX2nWvL&index=3
list - pass bunch of data to a step, useful when only 2 columns and header is optional 
@When("^I enter invalid data on the page$") 
   public void enterData(DataTable table){ 
      List<List<String>> data = table.raw();
      for(int i=1; i<data.size(); i++) {
         data.get(1).get(0) - gets the first value if there is header for Data table, else data.get(0).get(0)
         }
map - execute same step multiple times, can have mutliple columns in data table and in thsi header is needed for readability
@When("^I enter invalid data on the page$") 
   public void enterData(DataTable table){ 
      List<Map<String,String>> data = table.asMaps(String.class,String.class);
      for(int i=1; i<data.size(); i++) {
         data.get("FirstColHeader") 
         }

pojo - Class defined with members of same name as col headers, constructor that takes in the members and with getters and setters for each member - POJO; and the step definition will have 

Method that takes in param a List of that object type.
@When("^I enter invalid data on the page$") 
   public void enterData(List<ClassName> classObj){  or accept DataTable as param and do a dataTable.asList(ClassName.class)
   for(ClassName:obj){
         obj.headerName ;
         }
         }


sceanrio outline with examples - execute scenario mumtiple times
Background - run before every scenario in that feature file
tags - There are default tags that cucumber provides - @Dev, @Ignore, @wip. They are mentioned in the feature file and then used in Runner class to specify which test to run or not.
cucumber.embed to take screenshots and embed into reports
hooks-  Before, After, user defined tags - using tags in Runner class, using it with @Before and @After
@Before -  executed before ervey scenario === @BeforeMethod in TestNG - add this to any method in any step defs file and this will be run before every scneario in all the feature files.
@After - excetuted after every scenario unless tagged, will be executed even if the test fails
@After({"UserDefinedTagForScenario"}) - means that the method will run only after a scenario tagged with this name
Tags from sceanrio can be also used in Runner class, tags = {"@tagName"} to specify the tests that needs to be run === groups in TestNG
DocStrings - used to pass a paragrph of text to step definition file, accepted as a String
Then I should see this:
"""
blah blah blah
"""
in step definition, regualr expression will not mention anyhting and the docstring is taken in as a string param
@And("^Then I should see this$")
public void seeThis(String text){
    
}


Jenkins
--------
Download war
java -jar <war>
go to http://localhost:8080
give default pwd generated
set up all per instructions, choose plugins
create admin user

Logs and Jobs - C:\Users\10254634\.jenkins
To configure workspace, Configure Job - General - Advanced - Use custom WS - give your folder that you use as workspace.
Once this is done and a new job is triggered, you will see all the files in the Jenkins Workspace.
If this is done then you donot have to cd to the folder in the Execute Windows Batch Command window.
Whatever script is written in batch command window works same as if its run from command line
To configure reports, generate reports in junit xml format and configure the file name under Post build Actions selecting Publish Junit report. Once this is set, then you can see Latest Test Result link in the Job and also Test Result Trend. Jenkins understands by default Junit XML report format.


Saucelabs - https://wiki.saucelabs.com/display/DOCS/Instant+Selenium+Java+Tests
---------
Selenium or Appium test can be run
Test should invoke RemoteWebDriver
the aplication shd be accessible over internet - so use VMs on cloud.
Set the DC for the test- combination of platform, OS, Browser to be run
public static final String URL = "http://" + USERNAME + ":" + ACCESS_KEY + "@ondemand.us-east-1.saucelabs.com/wd/hub";
 WebDriver driver = new RemoteWebDriver(
                new URL(URL),
                capabilities);
USERNAME and ACESS_KEY - are set as envt variables and added to PATH or defined in class.
Test should be set up to report results to Saucelab dashboard. Saucelab provides REST API PUT end points to update results to the dashboard. Test names, build number, test tags, pass fail etc.. can be logged.
Test can be run from your local or by logging into Saucelabs, you dont need to log in to run tests, you log in only if you want to watch the tests being run.
You can run many tests parallely, and it also is integrated with Jenkins, Bamboo and other CI. same tests across different browsers at same time or same tests parallely across different browser instances. For this reason its best for tests to be not dependant on each other and to not have dependencies on external files. SO have set up and tear down methods.
Saucelab tests run on VMs.

Maven
-----
mvn dependency:resolve - updates any package dependencies
mvn test-compile - compiles only tests
mvn test - runs only tests
https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
To run testNG tests, configure surefire plugin in pom and in its <configuration> <suiteXmlFiles><suiteXmlFile>mention the testng.xml file name if using testng.xml


GRadle
------
Built over ant and maven, uses groovy based DSL instead of xml in Maven
To run testNG tests - under eah of the test task (based on different testsuites), useTestNG() -  mention include and exclude groups

Appium
-------
Appium is a HTTP server written in node.js which creates and handles multiple WebDriver sessions for different platforms like iOS and Android, similar to webdriver server.
it runs on any of the languages that web driver supports it is developed and supported by saucelabs.
Same tests can be run on android or ios


Selendroid - built over selenium, can be used to run only android tests.

Wait - I - has one abstract method <T> until (FunctionalI F); - which has only one method -  R apply(T t);
FluentWait implements Wait I, implements until and has lots of fluent methods, and is still flexiblke with types
WebDriverWait extends FluentWait - and has many constructors.

ExpectedCondition<T> is itself a Functional I  extends Function<WebDriver, T>
which means its implementations should have apply method with first param as WebDriver and second can be anything.
public interface ExpectedCondition<T> extends Function<WebDriver, T> {
}
This Function<WebDriver, T> --> is from google.guava and that itself extends java util Function
public interface Function<F, T> extends java.util.function.Function<F, T> {
  @Override
  
  T apply(@Nullable F input);
  }

  Usually ExpectedCOndition or ExpectedConditions is passed as param to FluentWait.until or WebdriverWait.until
methods in ExpectedConsitions returns either ExpectedCondition<Boolean> or ExpectedCondition<WebElement>

ExpectedConditions has many methods that returns a ExpectedCondition.
So either pass in ExpectedConditions or have your own implementation of Function I.
https://www.softwaretestingmaterial.com/selenium-fluentwait/

CssSelector
#a b - any b anywhere under a, can be a - c - b
#a > b - any b that is directly under a, only a - b
#a + b - any b  that is sibling with a ; they have same parent



