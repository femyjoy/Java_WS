package from_ecom.browser

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

public class VariantSection {
    private final WebElement webElement;
    private static final String CONNECTIVITY_SECTION_SELECTOR = "[data-qa-variants-connectivity-section]";
    private static final String SIZE_SECTION_SELECTOR = "[data-qa-variants-size-section]";
    private final boolean isAccessory

    public VariantSection(WebElement webElement) {
        this.webElement = webElement;
        this.isAccessory = webElement.getAttribute("class").contains("accessory-options-container");
    }

    public VariantSection selectColourByName(String name) {
        colour().selectByName(name);
        return this;
    }

    public VariantSection selectColourByIndex(int index) {
        colour().selectByIndex(index);
        return this;
    }

    public VariantSection selectCapacityByName(String capacityName) {
        capacity().selectByName(capacityName)
        return this;
    }

    public VariantSection selectWifi() {
        connectivity().selectWifi();
        return this;
    }

    public VariantSection select4G() {
        connectivity().select4G();
        return this;
    }

    public VariantSection selectSizeByName(String name) {
        size().selectByName(name)
        this
    }

    public colour() {
        WebElement colourSelector = webElement.findElement(By.cssSelector("[data-qa-variants-colour-section]"));
        isAccessory ? new AccessoryVariantSelectors(colourSelector,"colour") : new ColourSelectors(colourSelector);
    }

    public capacity() {
        WebElement capacitySelector = webElement.findElement(By.cssSelector("[data-qa-variants-capacity-section]"));
        isAccessory ? new AccessoryVariantSelectors(capacitySelector,"capacity") : new CapacitySection(capacitySelector);
    }

    public connectivity() {
        WebElement connectivitySelector = webElement.findElement(By.cssSelector(CONNECTIVITY_SECTION_SELECTOR));
        return new ConnectivitySelectors(connectivitySelector);
    }

    public size() {
        WebElement sizeSelector = webElement.findElement(By.cssSelector(SIZE_SECTION_SELECTOR))
        new AccessoryVariantSelectors(sizeSelector,"size")
    }

    public boolean hasConnectivitySelectors() {
        def connectivitySelectors = webElement.findElements(By.cssSelector(CONNECTIVITY_SECTION_SELECTOR))
        return connectivitySelectors && connectivitySelectors.size() == 1
    }

    public boolean hasSizeSelectors() {
        def sizeSelectors = webElement.findElements(By.cssSelector(SIZE_SECTION_SELECTOR))
        return sizeSelectors && sizeSelectors.size() == 1
    }

    public boolean hasColourSelectors() {
        def colourSelector = webElement.findElements(By.cssSelector("[data-qa-variants-colour-section]"))
        return colourSelector && colourSelector.size() == 1
    }

    public boolean hasCapacitySelectors() {
        def capacitySelector = webElement.findElements(By.cssSelector("[data-qa-variants-capacity-section]"))
        return capacitySelector && capacitySelector.size() == 1
    }

}
