package com.test.contacts.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class ContactsListPage extends BasePage {

    // '+' floating button (Google Contacts)
    private final List<By> addContactLocators = List.of(
            byDescription("Create contact"),
            AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*floating_action_button.*\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Create\")")
    );

    // Tooltip shown only the first time: "Now you can find Settings and Help & feedback here"
    private final By settingsTooltip = byTextContains("Now you can find");

    // Instant check (no waiting): only dismiss it if it is already on screen
    public ContactsListPage dismissTooltipIfPresent() {
        if (isPresent(settingsTooltip)) {
            tapScreenCenter();
            log.info("Tooltip dismissed");
        }
        return this;
    }

    public ContactFormPage tapAddContact() {
        WebElement addButton = waitForFirst(addContactLocators, Duration.ofSeconds(15), "Create contact (+)");
        if (isPresent(settingsTooltip)) {
            dismissTooltipIfPresent();
            addButton = waitForFirst(addContactLocators, Duration.ofSeconds(5), "Create contact (+)");
        }
        addButton.click();
        log.info("Step 4: Tapped '+' (Create contact)");
        return new ContactFormPage();
    }
}