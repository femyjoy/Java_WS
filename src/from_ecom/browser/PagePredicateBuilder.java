package from_ecom.browser;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import uk.co.o2.seleniumHtmlFramework.MissingWebElement;
import uk.co.o2.seleniumHtmlFramework.elements.Div;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.By.id;

public class PagePredicateBuilder {
    private final List<Predicate<WebDriver>> predicates = new ArrayList<>();

    private PagePredicateBuilder() {
    }

    public static PagePredicateBuilder newCondition() {
        return new PagePredicateBuilder();
    }

    public static PagePredicateBuilder pageIsReached() {
        return newCondition();
    }

    public static PagePredicateBuilder pageIsReady() {
        return new PagePredicateBuilder()
                .whenBodyIsLoaded();
    }

    public Predicate<WebDriver> build() {
        return new AllOfCompositePredicate(predicates);
    }

    public PagePredicateBuilder whenBodyIsLoaded() {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.findElement(By.tagName("body")) != null;
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenAddressesBoxIsLoaded() {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.findElement(By.xpath("//*[@id=\"delivery-address-selection\"]")) != null;
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenAddressesErrorIsLoaded() {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.findElement(By.xpath("//*[@id=\"address-form-error\"]/label")) != null;
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenAccessoriesSectionChangedTo(final String className) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.findElement(id("qa-open-accessory")).getAttribute("class").equals(className);
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenJavaScriptIsReady() {
        return whenJavaScriptEvaluates("return window.pageReady;");
    }

    public PagePredicateBuilder whenAngularIsDone() {
        return whenJavaScriptEvaluates("return isAngularDone;");
    }

    public PagePredicateBuilder homeWelcomeMessageLoaded() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement welcomeMessage = driver.findElement(By.id("welcome-message"));

                return !(welcomeMessage.getAttribute("class").contains("pending")) ;
            }
        });

        return this;
    }

    public PagePredicateBuilder myOffersHomePageLoaded() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement element =  driver.findElement(By.cssSelector("[data-qa-page-heading]"));
                return element.isEnabled();
            }
        });

        return this;
    }

    public PagePredicateBuilder upgradingMsisdnInfoIsLoaded() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                boolean isBrowsingJourney = driver.getCurrentUrl().contains("upgrade/store/");
                if (!isBrowsingJourney) {
                    WebElement upgradingMsisdn = driver.findElement(By.cssSelector(".upgrading-msisdn"));
                    return !(upgradingMsisdn.getAttribute("class").contains("pending")) ;
                } else {
                    return true;
                }


            }
        });

        return this;
    }


    public PagePredicateBuilder detailsPageDockerLoaded() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement docker = driver.findElement(By.cssSelector(".docked"));

                return (docker.getAttribute("class").contains("docked")) ;
            }
        });

        return this;
    }

    public PagePredicateBuilder whenJavaScriptEvaluates(final String javaScript) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        Object result = ((RemoteWebDriver) driver).executeScript(javaScript);
                        return result == null ? false : (Boolean) result;
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenBodyContains(final String expectedText) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        WebElement body = driver.findElement(By.tagName("body"));
                        String bodyText = body == null ? "[NO BODY FOUND!]" : body.getText();
                        return bodyText.contains(expectedText);
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenHeaderContains(final String expectedText) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        PageHelper helper = new PageHelper((RemoteWebDriver) driver);
                        return helper.getHeaderText().contains(expectedText);
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenElementIsPresent(final By selector) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.findElement(selector) != null;
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenPageUrlContains(final String expectedUrl) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.getCurrentUrl().contains(expectedUrl);
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenPageTitleContains(final String expectedTitle) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.getTitle().contains(expectedTitle);
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenPageTitleStartsWith(final String expectedTitle) {
        predicates.add(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        return driver.getTitle().startsWith(expectedTitle);
                    }
                }
        );
        return this;
    }

    public PagePredicateBuilder whenElementHasClass(final WebElement element, final String className) {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return element.getAttribute("class").contains(className);
            }
        });

        return this;
    }

    public PagePredicateBuilder whenElementIsVisible(final WebElement element) {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return element.isDisplayed();
            }
        });

        return this;
    }

    public PagePredicateBuilder whenPageStopsScrolling() {

        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                if (driver.findElement(By.tagName("body")).getAttribute("class").contains("scrolling")) {
                    return false;
                }
                return true;
            }
        });

        return this;
    }

    public PagePredicateBuilder whenDivHasNoClass(final Div element, final String className) {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                if (!element.isMissing()) {
                    return !element.attribute("class").matches(".*\\b" + className + "\\b.*");
                }
                return true;
            }
        });
        return this;
    }

    public PagePredicateBuilder refreshStarted() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement chart = driver.findElement(By.className("chart"));
                WebElement currentPackage = driver.findElement(By.className("package-details-table"));

                return chart.getAttribute("class").contains("pending") && currentPackage.getAttribute("class").contains("pending");
            }
        });

        return this;
    }

    public PagePredicateBuilder usageDoNutChartsRefreshed() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement data = driver.findElement(By.id("data-donut-element"));
                WebElement minutes = driver.findElement(By.id("minutes-donut-element"));
                WebElement texts = driver.findElement(By.id("texts-donut-element"));

                return !(data.getAttribute("class").contains("refreshing") || minutes.getAttribute("class").contains("refreshing") || texts.getAttribute("class").contains("refreshing")) ;
            }
        });

        return this;
    }

    public PagePredicateBuilder currentPackageSectionRefreshed() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement currentPackageElement = findCurrentPackageElement(driver);
                return currentPackageElement.isDisplayed() && !currentPackageElement.getAttribute("class").contains("pending");

            }

            private WebElement findCurrentPackageElement(WebDriver driver) {
                //need to do this, as same section is displayed multiple times on the page due to responsive design constraints
                for (WebElement webElement : driver.findElements(By.className("package-details-table"))) {
                    if (webElement.isDisplayed()) {
                        return webElement;
                    }
                }

                return new MissingWebElement();
            }
        });

        return this;
    }

    public PagePredicateBuilder whenCnCSelectedStoreLoaded() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement tariifPage = driver.findElement(By.id("header-primary"));

                return !(tariifPage.getAttribute("class").contains("pending")) ;
            }
        });

        return this;
    }

    public PagePredicateBuilder angularDigestCompleted() {
        return whenJavaScriptEvaluates("return _docReady();");
    }

    public PagePredicateBuilder linksToAbandonFinishOrderDisplayed() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                WebElement finishOrderLink = driver.findElement(By.id("finishOrder"));
                return finishOrderLink.isDisplayed();
            }
        });
    return this;
    }
    public PagePredicateBuilder linksToAbandonCheckoutDisplayed() {
        predicates.add(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {

                WebElement abandonCheckoutLink = driver.findElement(By.id("abandonCheckout"));
                return abandonCheckoutLink.isDisplayed();
            }
        });
    return this;
    }
}
