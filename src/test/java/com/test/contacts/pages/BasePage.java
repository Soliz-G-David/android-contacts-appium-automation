package com.test.contacts.pages;

import com.test.contacts.utils.ConfigReader;
import com.test.contacts.utils.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
    }

    // ---------- Basic actions ----------

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // ---------- Locators ----------

    protected By byText(String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + text + "\")");
    }

    protected By byTextContains(String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + text + "\")");
    }

    protected By byDescription(String description) {
        return AppiumBy.androidUIAutomator("new UiSelector().description(\"" + description + "\")");
    }

    // Material text fields show their label as text (empty field) or as hint
    protected By inputByLabel(String label) {
        return AppiumBy.xpath("//android.widget.EditText[@text='" + label + "' or @hint='" + label + "']");
    }

    protected By scrollToText(String text) {
        return AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");
    }

    // ---------- Multiple locator strategies ----------

    protected WebElement findFirst(List<By> locators) {
        for (By locator : locators) {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                return found.get(0);
            }
        }
        return null;
    }

    protected WebElement waitForFirst(List<By> locators, Duration timeout, String elementName) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            WebElement element = findFirst(locators);
            if (element != null) {
                return element;
            }
        }
        throw new NoSuchElementException("'" + elementName + "' not found after "
                + timeout.getSeconds() + " seconds");
    }

    protected WebElement scrollUntilVisible(By locator, int maxSwipes, String elementName) {
        for (int i = 0; i <= maxSwipes; i++) {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                return found.get(0);
            }
            hideKeyboardIfVisible();
            swipeVertical(0.65, 0.35);
        }
        throw new NoSuchElementException("'" + elementName + "' not found after "
                + maxSwipes + " swipes");
    }

    // ---------- Gestures and keyboard ----------

    protected void tapAt(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
        log.info("Tapped at ({}, {})", x, y);
    }

    protected void tapScreenCenter() {
        Dimension size = driver.manage().window().getSize();
        tapAt(size.width / 2, size.height / 2);
    }

    protected void swipeVertical(double startPercent, double endPercent) {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int startY = (int) (size.height * startPercent);
        int endY = (int) (size.height * endPercent);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(swipe));
        log.info("Swipe performed from {} to {}", startY, endY);
    }

    protected void typeWithKeyboard(String text) {
        new Actions(driver).sendKeys(text).perform();
        log.info("Typed with keyboard: {}", text);
    }

    protected void hideKeyboardIfVisible() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception e) {
            log.debug("Keyboard could not be hidden: {}", e.getMessage());
        }
    }
}