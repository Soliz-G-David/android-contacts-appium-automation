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

    // "Search contacts" bar at the top of the list
    private final List<By> searchBarLocators = List.of(
            byText("Search contacts"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Search contacts\")"),
            AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*search.*\")")
    );

    private final By searchInput = AppiumBy.className("android.widget.EditText");

    // Tooltip shown only the first time: "Now you can find Settings and Help & feedback here"
    private final By settingsTooltip = byTextContains("Now you can find");

    public ContactsListPage() {
        waitForFirst(addContactLocators, Duration.ofSeconds(15), "Create contact (+)");
        log.info("Contacts list is displayed");
    }

    // Instant check (no waiting): only dismiss it if it is already on screen
    public ContactsListPage dismissTooltipIfPresent() {
        if (isPresent(settingsTooltip)) {
            tapScreenCenter();
            log.info("Tooltip dismissed");
        }
        return this;
    }

    public ContactFormPage tapAddContact() {
        dismissTooltipIfPresent();
        WebElement addButton = waitForFirst(addContactLocators, Duration.ofSeconds(5), "Create contact (+)");
        addButton.click();
        log.info("Tapped '+' (Create contact)");
        return new ContactFormPage();
    }

    // Checks the contact appears in the list (scrolls if the list is long)
    public boolean isContactDisplayed(String fullName) {
        boolean ok = isDisplayed(byText(fullName), Duration.ofSeconds(5))
                || isDisplayed(scrollToText(fullName), Duration.ofSeconds(5));
        log.info("List check -> '{}' displayed: {}", fullName, ok);
        return ok;
    }

    // Search contacts -> type query -> open the matching contact
    public ContactDetailPage searchAndOpenContact(String query, String fullName) {
        dismissTooltipIfPresent();

        WebElement searchBar = waitForFirst(searchBarLocators, Duration.ofSeconds(10), "Search contacts");
        searchBar.click();
        log.info("Tapped 'Search contacts'");

        waitForVisible(searchInput).sendKeys(query);
        log.info("Typed '{}' in search", query);

        By result = AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.TextView\").text(\"" + fullName + "\")");
        click(result);
        log.info("Opened search result '{}'", fullName);

        return new ContactDetailPage();
    }
}