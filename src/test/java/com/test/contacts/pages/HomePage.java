package com.test.contacts.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class HomePage extends BasePage {

    private static final String CONTACTS_APP = "Contacts";

    // 'Contacts' icon inside the app drawer
    private final List<By> contactsIconLocators = List.of(
            AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.TextView\").text(\"" + CONTACTS_APP + "\")"),
            AppiumBy.androidUIAutomator(
                    "new UiSelector().description(\"" + CONTACTS_APP + "\")")
    );

    public HomePage goToHome() {
        driver.pressKey(new KeyEvent(AndroidKey.HOME));
        log.info("Step 1: Home screen");
        return this;
    }

    // Swipe up from the empty middle area (not over the icons) to open the app drawer
    public HomePage openAppDrawer() {
        swipeVertical(0.50, 0.10);
        log.info("Step 2: Swipe up to open app drawer");

        if (findFirstWithin(Duration.ofSeconds(3)) == null) {
            log.info("Contacts icon not visible yet, swiping up again");
            swipeVertical(0.50, 0.10);
        }
        return this;
    }

    public ContactsListPage tapContactsIcon() {
        WebElement icon = findFirstWithin(Duration.ofSeconds(5));
        if (icon == null) {
            // Contacts may be further down the alphabetical list: scroll inside the drawer
            icon = scrollUntilVisible(contactsIconLocators.get(0), 3, CONTACTS_APP);
        }
        icon.click();
        log.info("Step 3: Tapped Contacts icon");
        return new ContactsListPage();
    }

    // Full navigation: Home -> swipe up -> tap Contacts
    public ContactsListPage openContactsApp() {
        return goToHome()
                .openAppDrawer()
                .tapContactsIcon();
    }

    private WebElement findFirstWithin(Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            WebElement element = findFirst(contactsIconLocators);
            if (element != null) {
                return element;
            }
        }
        return null;
    }
}