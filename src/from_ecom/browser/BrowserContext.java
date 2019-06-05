package from_ecom.browser;

public class BrowserContext {
    private final String hostUrl;
    private final String screenShotPath;

    public BrowserContext(String screenShotPath) {
        this.screenShotPath = screenShotPath;
        String server = System.getProperty("qa.hostName", "localhost");
        String serverPort = System.getProperty("qa.hostPort", "8080");
        hostUrl = "http://" + server + ":" + serverPort;
    }

    public String buildFullUrl(String pageUrl) {
        return hostUrl + "/ssc" + pageUrl;
    }

    public String screenshotPath() {
        return screenShotPath;
    }
}
