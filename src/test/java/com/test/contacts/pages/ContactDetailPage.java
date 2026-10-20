package com.test.contacts.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class ContactDetailPage extends BasePage {

    private static final String CONTACT_INFO = "Contact info";
    private static final String ADDED_PREFIX = "Added ";
    private static final int MAX_BACK_PRESSES = 4;

    // Back arrow at the top-left of the detail screen
    private final List<By> backArrowLocators = List.of(
            byDescription("Navigate up"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i)(navigate up|back)\")")
    );

    // Pencil icon at the top of the detail screen
    private final List<By> editLocators = List.of(
            byDescription("Edit contact"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Edit\")")
    );

    // The full contacts list shows the empty "Search contacts" bar and the '+' button
    private final By searchContactsBar = byText("Search contacts");
    private final By addContactButton = byDescription("Create contact");

    public ContactDetailPage() {
        waitForVisible(byText(CONTACT_INFO));
        log.info("Contact detail page is displayed");
    }

    public boolean isTextDisplayed(String text) {
        if (isDisplayed(byText(text), Duration.ofSeconds(3))) {
            return true;
        }
        return isDisplayed(scrollToText(text), Duration.ofSeconds(5));
    }

    // Instant check: the text must not be on the screen
    public boolean isTextNotDisplayed(String text) {
        boolean absent = !isPresent(byText(text));
        log.info("Absence check -> '{}' not displayed: {}", text, absent);
        return absent;
    }

    // "Added Oct 20, 2026": proves the contact was created today (date taken from the emulator)
    public boolean isAddedToday() {
        String today = driver.getDeviceTime("MMM D, YYYY");
        String expected = ADDED_PREFIX + today;
        boolean ok = isTextDisplayed(expected);
        log.info("Creation date check -> expected: '{}', result: {}", expected, ok);
        return ok;
    }

    // Android formats the number, so compare digits only
    public boolean isPhoneDisplayed(String phone) {
        String expectedDigits = phone.replaceAll("\\D", "");
        List<WebElement> texts = driver.findElements(AppiumBy.className("android.widget.TextView"));
        for (WebElement text : texts) {
            String digits = text.getText().replaceAll("\\D", "");
            if (!digits.isEmpty() && digits.equals(expectedDigits)) {
                log.info("Phone found as: '{}'", text.getText());
                return true;
            }
        }
        return false;
    }

    // Tap the pencil icon to edit the contact
    public ContactFormPage tapEdit() {
        WebElement edit = waitForFirst(editLocators, Duration.ofSeconds(10), "Edit contact");
        edit.click();
        log.info("Tapped Edit contact (pencil)");
        return new ContactFormPage();
    }

    // One back: detail -> list (used right after creating a contact)
    public ContactsListPage goBackToList() {
        pressBack();
        return new ContactsListPage();
    }

    // Back as many times as needed (detail -> search results -> full list), never leaving the app
    public ContactsListPage returnToContactsList() {
        for (int i = 1; i <= MAX_BACK_PRESSES; i++) {
            pressBack();
            if (isFullListDisplayed(Duration.ofSeconds(3))) {
                log.info("Back to full contacts list after {} back press(es)", i);
                return new ContactsListPage();
            }
        }
        throw new NoSuchElementException("Contacts list not reached after " + MAX_BACK_PRESSES + " back presses");
    }

    // ---------- helpers ----------

    private void pressBack() {
        WebElement backArrow = findFirst(backArrowLocators);
        if (backArrow != null) {
            backArrow.click();
            log.info("Tapped back arrow");
        } else {
            driver.navigate().back();
            log.info("Used Android back button");
        }
    }

    private boolean isFullListDisplayed(Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            if (isPresent(searchContactsBar) && isPresent(addContactButton)) {
                return true;
            }
        }
        return false;
    }
}