package com.test.contacts.pages;

import com.test.contacts.models.Contact;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ContactFormPage extends BasePage {

    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String COMPANY = "Company";
    private static final String PHONE = "Phone";
    private static final String EMAIL = "Email";

    private final By saveButton = byText("Save");
    private final By anyInput = AppiumBy.className("android.widget.EditText");

    // Works for both Create (empty form) and Edit (filled form)
    public ContactFormPage() {
        waitForVisible(anyInput);
        log.info("Contact form is displayed");
    }

    // ---------- Create ----------

    public ContactFormPage enterFirstName(String value) {
        return enterField(FIRST_NAME, value);
    }

    public ContactFormPage enterLastName(String value) {
        return enterField(LAST_NAME, value);
    }

    public ContactFormPage enterCompany(String value) {
        return enterField(COMPANY, value);
    }

    public ContactFormPage enterPhone(String value) {
        return enterField(PHONE, value);
    }

    // After Phone: hide keyboard, short scroll up, then fill Email
    public ContactFormPage enterEmail(String value) {
        shortScrollUp();
        return enterField(EMAIL, value);
    }

    public ContactFormPage fillContactForm(Contact contact) {
        log.info("Filling form for {}", contact.fullName());
        return enterFirstName(contact.firstName())
                .enterLastName(contact.lastName())
                .enterCompany(contact.company())
                .enterPhone(contact.phone())
                .enterEmail(contact.email());
    }

    // ---------- Edit ----------

    public ContactFormPage updateContact(Contact current, Contact updated) {
        log.info("Updating '{}' -> '{}'", current.fullName(), updated.fullName());
        updateField(FIRST_NAME, current.firstName(), updated.firstName());
        updateField(LAST_NAME, current.lastName(), updated.lastName());
        updateField(COMPANY, current.company(), updated.company());
        updateField(PHONE, current.phone(), updated.phone());
        shortScrollUp();
        updateField(EMAIL, current.email(), updated.email());
        return this;
    }

    // ---------- Save ----------

    public ContactDetailPage save() {
        hideKeyboardIfVisible();
        click(saveButton);
        log.info("Tapped Save");
        return new ContactDetailPage();
    }

    // ---------- helpers ----------

    private ContactFormPage enterField(String label, String value) {
        hideKeyboardIfVisible();
        WebElement field = scrollUntilVisible(inputByLabel(label), 4, label);
        field.sendKeys(value);
        log.info("  {} = {}", label, value);
        return this;
    }

    // Finds the field by its label (hint) or by its current value, clears it and types the new one
    private void updateField(String label, String currentValue, String newValue) {
        hideKeyboardIfVisible();
        WebElement field = scrollUntilVisible(filledInput(label, currentValue), 4, label);
        field.clear();
        field.sendKeys(newValue);
        log.info("  {}: '{}' -> '{}'", label, currentValue, newValue);
    }

    // Phone is shown formatted, e.g. (707) 084-06, so it is also compared without symbols
    private By filledInput(String label, String currentValue) {
        return AppiumBy.xpath("//android.widget.EditText["
                + "@hint='" + label + "' or @text='" + label + "'"
                + " or @text='" + currentValue + "'"
                + " or translate(@text, '()-. ', '')='" + currentValue + "']");
    }

    private void shortScrollUp() {
        hideKeyboardIfVisible();
        swipeVertical(0.60, 0.40);
        log.info("  Short scroll up to reveal Email");
    }
}