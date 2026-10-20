package com.test.contacts.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.time.Duration;
import java.util.List;
import java.util.Map;

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

    // Trash icon shown in selection mode
    private final List<By> deleteIconLocators = List.of(
            byDescription("Delete"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Delete\")"),
            AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*delete.*\")")
    );

    // 'Delete' button in the "Delete contacts" dialog
    private final List<By> confirmDeleteLocators = List.of(
            AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").text(\"Delete\")"),
            byText("Delete")
    );

    private final By noContactsMessage = byText("No contacts yet");

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

    // ---------- Create ----------

    public ContactFormPage tapAddContact() {
        dismissTooltipIfPresent();
        WebElement addButton = waitForFirst(addContactLocators, Duration.ofSeconds(5), "Create contact (+)");
        addButton.click();
        log.info("Tapped '+' (Create contact)");
        return new ContactFormPage();
    }

    // ---------- Read ----------

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

        click(contactItem(fullName));
        log.info("Opened search result '{}'", fullName);

        return new ContactDetailPage();
    }

    // ---------- Delete ----------

    // Long press on the first contact to start selection, then tap the others
    public ContactsListPage selectContacts(List<String> fullNames) {
        WebElement first = waitForVisible(contactItem(fullNames.get(0)));
        driver.executeScript("mobile: longClickGesture", Map.of(
                "elementId", ((RemoteWebElement) first).getId(),
                "duration", 1000));
        log.info("Long press on '{}' (1 selected)", fullNames.get(0));

        for (int i = 1; i < fullNames.size(); i++) {
            click(contactItem(fullNames.get(i)));
            log.info("Selected '{}' ({} selected)", fullNames.get(i), i + 1);
        }
        return this;
    }

    public boolean isSelectedCountDisplayed(int count) {
        String expected = count + " selected";
        boolean ok = isDisplayed(byText(expected), Duration.ofSeconds(5));
        log.info("Selection check -> '{}' displayed: {}", expected, ok);
        return ok;
    }

    // Trash icon -> "Delete contacts" dialog -> Delete
    public ContactsListPage deleteSelectedContacts() {
        waitForFirst(deleteIconLocators, Duration.ofSeconds(10), "Delete (trash icon)").click();
        log.info("Tapped trash icon");

        waitForFirst(confirmDeleteLocators, Duration.ofSeconds(10), "Delete (dialog button)").click();
        log.info("Confirmed 'Delete' in dialog");
        return this;
    }

    public boolean isNoContactsMessageDisplayed() {
        boolean ok = isDisplayed(noContactsMessage, Duration.ofSeconds(10));
        log.info("Empty list check -> 'No contacts yet' displayed: {}", ok);
        return ok;
    }

    // ---------- helpers ----------

    private By contactItem(String fullName) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.TextView\").text(\"" + fullName + "\")");
    }
}