package from_ecom.browser;


import org.openqa.selenium.remote.RemoteWebDriver;

import static java.lang.Runtime.getRuntime;

public class TestContext {
    private RemoteWebDriver desktopDriver = null;
    private WebBrowser desktopBrowser = null;

    private RemoteWebDriver mobileDriver = null;
    private WebBrowser mobileBrowser = null;

    private String browserType;
    private String browserVersion;
    private boolean isMobileJourney;

    private ShutdownHook shutdownHook;

    public TestContext() {
        browserType = System.getProperty("qa.desktopBrowser", "firefox");
        browserVersion = System.getProperty("qa.browserVersion", "22");
        isMobileJourney = browserType.equalsIgnoreCase("iphoneCI");
    }

    private static final ThreadLocal<TestContext> context =
            new ThreadLocal<TestContext>() {
                @Override
                protected TestContext initialValue() {
                    return new TestContext();
                }
            };

    public static RemoteWebDriver getDriver() {
        if (isMobileJourney()) {
            return context.get().mobileDriver;
        } else {
            return context.get().desktopDriver;
        }

    }

    public static void setDriver(RemoteWebDriver driver) {
        if (isMobileJourney()) {
            context.get().mobileDriver = driver;
            context.get().mobileBrowser = new WebBrowser(driver);
        } else {
            context.get().desktopDriver = driver;
            context.get().desktopBrowser = new WebBrowser(driver);
        }

        if (context.get().shutdownHook == null){
            context.get().shutdownHook = context.get().new ShutdownHook();
            getRuntime().addShutdownHook(context.get().shutdownHook);
        }

    }

    public static WebBrowser getBrowser() {
        if (isMobileJourney()) {
            return context.get().mobileBrowser;
        } else {
            return context.get().desktopBrowser;
        }
    }

    public static void setBrowser(WebBrowser browser) {
        if (isMobileJourney()) {
            context.get().mobileBrowser = browser;
        } else {
            context.get().desktopBrowser = browser;
        }
    }

    public static void quitBrowser() {
        if (isMobileJourney()) {
            context.get().mobileDriver.quit();
            context.get().mobileBrowser = null;
        } else {
            context.get().desktopDriver.quit();
            context.get().desktopBrowser = null;
        }
    }

    public static void setBrowserType(String browserType) {
        context.get().browserType = browserType;
        context.get().isMobileJourney = browserType.equalsIgnoreCase("iphoneCI");
    }

    public static String getBrowserVersion() {
        return context.get().browserVersion;
    }

    public static boolean isMobileJourney() {
        return context.get().isMobileJourney;
    }

    public class ShutdownHook extends Thread {
        @Override
        public void run() {
            try {
                if (desktopDriver != null){
                    desktopDriver.quit();
                    desktopDriver = null;
                }

                if (mobileDriver != null){
                    mobileDriver.quit();
                    mobileDriver = null;
                }
            } catch (Exception e) {
                //ignore this error - there is something else that shuts down the browser as well, so we get errors here
            }
        }
    }
}
