package com.test.contacts.pages;

import com.test.contacts.models.Contact;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

public class ContactFormPage extends BasePage {

    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String COMPANY = "Company";
    private static final String PHONE = "Phone";
    private static final String EMAIL = "Email";

    private final By saveButton = byText("Save");
    private final By anyInput = AppiumBy.className("android.widget.EditText");

    // ---------- Picture locators ----------

    private final List<By> addPictureLocators = List.of(
            byText("Add picture"),
            byDescription("Add picture"),
            AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)(add|change|edit) picture\")"),
            AppiumBy.androidUIAutomator("new UiSelector().descriptionMatches(\"(?i).*(add|change|edit) picture.*\")")
    );

    private final List<By> galleryLocators = List.of(
            byText("Gallery"),
            byDescription("Gallery")
    );

    // 'Choose' button shown after the photo loads
    private final By chooseButton = AppiumBy.androidUIAutomator(
            "new UiSelector().textMatches(\"(?i)(choose|done|crop|ok)\")");

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

    // ---------- Picture ----------

    // Add picture -> Gallery -> Recent: pick the photo -> Choose -> back to Edit contact
    public ContactFormPage addPictureFromGallery(String fileName) {
        hideKeyboardIfVisible();

        waitForFirst(addPictureLocators, Duration.ofSeconds(10), "Add picture").click();
        log.info("Tapped 'Add picture'");

        waitForFirst(galleryLocators, Duration.ofSeconds(10), "Gallery").click();
        log.info("Tapped 'Gallery'");

        selectPhotoInRecent(fileName);

        tapChooseUntilBackToForm();
        log.info("Picture added, back to Edit contact");
        return this;
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

    // ---------- helpers: picture ----------

    // By file name first; if Recent shows only thumbnails, take the first one (the image pushed by the test)
    private void selectPhotoInRecent(String fileName) {
        String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;

        List<By> byName = List.of(
                byText(fileName),
                AppiumBy.androidUIAutomator("new UiSelector().textContains(\"" + baseName + "\")"),
                AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"" + baseName + "\")")
        );

        List<By> firstThumbnail = List.of(
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*icon_thumb.*\")"),
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*thumbnail.*\")"),
                AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*item_root.*\")"),
                AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Photo taken\")")
        );

        try {
            WebElement photo = findFirstWithin(byName, Duration.ofSeconds(5));
            if (photo != null) {
                photo.click();
                log.info("Selected '{}' in Recent by name", fileName);
                return;
            }
            waitForFirst(firstThumbnail, Duration.ofSeconds(10), "First photo in Recent").click();
            log.info("Name not shown in Recent, selected the first (most recent) photo");
        } catch (NoSuchElementException e) {
            saveScreenDiagnostics("recent-screen");
            throw e;
        }
    }

    // The photo takes a moment to load: wait for 'Choose', tap it, and wait for the form to return.
    // The Edit contact form has EditText fields; the photo screen does not.
    private void tapChooseUntilBackToForm() {
        long end = System.currentTimeMillis() + Duration.ofSeconds(20).toMillis();
        while (System.currentTimeMillis() < end) {
            if (isPresent(anyInput)) {
                return;
            }
            List<WebElement> choose = driver.findElements(chooseButton);
            if (!choose.isEmpty()) {
                log.info("Tapped '{}'", choose.get(0).getText());
                choose.get(0).click();
            }
        }
        saveScreenDiagnostics("choose-screen");
        throw new NoSuchElementException("Did not return to Edit contact after tapping 'Choose'");
    }

    private WebElement findFirstWithin(List<By> locators, Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            WebElement element = findFirst(locators);
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    // Saves screenshot + UI hierarchy in build/ to find the right locator
    private void saveScreenDiagnostics(String name) {
        try {
            Path dir = Path.of("build", "diagnostics");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve(name + ".xml"), driver.getPageSource());
            File screenshot = driver.getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), dir.resolve(name + ".png"), StandardCopyOption.REPLACE_EXISTING);
            log.info("Diagnostics saved: build/diagnostics/{}.xml and .png", name);
        } catch (IOException e) {
            log.info("Could not save diagnostics: {}", e.getMessage());
        }
    }

    // ---------- helpers: fields ----------

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