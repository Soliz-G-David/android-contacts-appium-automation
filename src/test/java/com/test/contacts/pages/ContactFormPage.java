package com.test.contacts.pages;

import com.test.contacts.models.Contact;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ContactFormPage extends BasePage {

    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String COMPANY = "Company";
    private static final String PHONE = "Phone";
    private static final String EMAIL = "Email";

    private final By saveButton = byText("Save");

    public ContactFormPage() {
        waitForVisible(inputByLabel(FIRST_NAME));
        log.info("Create contact form is displayed");
    }

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
        hideKeyboardIfVisible();
        swipeVertical(0.60, 0.40);
        log.info("  Short scroll up to reveal Email");
        return enterField(EMAIL, value);
    }

    public ContactFormPage fillContactForm(Contact contact) {
        log.info("Step 5: Filling form for {}", contact.fullName());
        return enterFirstName(contact.firstName())
                .enterLastName(contact.lastName())
                .enterCompany(contact.company())
                .enterPhone(contact.phone())
                .enterEmail(contact.email());
    }

    public ContactDetailPage save() {
        hideKeyboardIfVisible();
        click(saveButton);
        log.info("Step 6: Tapped Save");
        return new ContactDetailPage();
    }

    private ContactFormPage enterField(String label, String value) {
        hideKeyboardIfVisible();
        WebElement field = scrollUntilVisible(inputByLabel(label), 4, label);
        field.sendKeys(value);
        log.info("  {} = {}", label, value);
        return this;
    }
}