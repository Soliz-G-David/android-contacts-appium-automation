package com.test.contacts.tests;

import com.test.contacts.base.BaseTest;
import com.test.contacts.models.Contact;
import com.test.contacts.pages.ContactDetailPage;
import com.test.contacts.pages.ContactFormPage;
import com.test.contacts.pages.ContactsListPage;
import com.test.contacts.pages.HomePage;
import com.test.contacts.utils.DeviceFileHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static com.test.contacts.utils.ReportHelper.attachScreenshot;
import static com.test.contacts.utils.ReportHelper.step;

public class CreateContactTest extends BaseTest {

    // ---------- Test data: create ----------
    private static final List<Contact> CONTACTS = List.of(
            new Contact("David", "Soliz", "AssureSoft", "70708406", "soliz.g.david@gmail.com"),
            new Contact("Joseph", "Colbert", "Unosquare", "71234567", "joseph.colbert@example.com"),
            new Contact("Oscar", "Segales", "Jalasoft", "76543210", "oscar.segales@example.com")
    );

    // ---------- Test data: search and update ----------
    private static final String SEARCH_TEXT = "Soliz";
    private static final Contact CONTACT_TO_UPDATE = CONTACTS.get(0);
    private static final Contact UPDATED = new Contact(
            "Davichin", "Gallagher", "CodeRoad", "777777777", "davichin90s@gmail.com");
    private static final String PROFILE_PICTURE = "src/test/resources/images/profile.jpg";
    private static final String PROFILE_PICTURE_NAME = "profile.jpg";

    // ---------- Test data: delete (contacts as they are after the update) ----------
    private static final List<String> CONTACTS_TO_DELETE = List.of(
            UPDATED.fullName(),
            CONTACTS.get(1).fullName(),
            CONTACTS.get(2).fullName()
    );

    // ---------- Shared state: the flow continues from one test to the next ----------
    private ContactsListPage listPage;
    private ContactDetailPage detailPage;
    private ContactFormPage formPage;

    // ===================== CREATE =====================

    @Test(priority = 1, groups = "Create",
            description = "TC-01: Create user 1 (David Soliz) and verify it in detail and list")
    public void tc01_createFirstContact() {
        step("Copy profile picture to the emulator gallery", () -> {
            DeviceFileHelper.pushImageToGallery(PROFILE_PICTURE);
        });
        step("Open Contacts: Home > swipe up > Contacts", () -> {
            listPage = new HomePage().openContactsApp();
        });
        createAndVerify(CONTACTS.get(0));
    }

    @Test(priority = 2, groups = "Create", dependsOnMethods = "tc01_createFirstContact",
            description = "TC-02: Create users 2 and 3 and verify all contacts in the list")
    public void tc02_createAdditionalContacts() {
        createAndVerify(CONTACTS.get(1));
        createAndVerify(CONTACTS.get(2));

        step("Verify the 3 contacts are in the list", () -> {
            attachScreenshot("Contacts list with 3 contacts");
            SoftAssert softAssert = new SoftAssert();
            for (Contact contact : CONTACTS) {
                softAssert.assertTrue(listPage.isContactDisplayed(contact.fullName()),
                        contact.fullName() + " should be in contacts list");
            }
            softAssert.assertAll();
        });
    }

    // ===================== SEARCH =====================

    @Test(priority = 3, groups = "Search", dependsOnMethods = "tc02_createAdditionalContacts",
            description = "TC-03: Search contact by last name 'Soliz' and open it")
    public void tc03_searchContact() {
        step("Search '" + SEARCH_TEXT + "' and open " + CONTACT_TO_UPDATE.fullName(), () -> {
            detailPage = listPage.searchAndOpenContact(SEARCH_TEXT, CONTACT_TO_UPDATE.fullName());
        });
        step("Verify the right contact was opened", () -> {
            attachScreenshot("Search result opened");
            Assert.assertTrue(detailPage.isTextDisplayed(CONTACT_TO_UPDATE.fullName()),
                    "Search should open '" + CONTACT_TO_UPDATE.fullName() + "'");
        });
    }

    // ===================== UPDATE =====================

    @Test(priority = 4, groups = "Update", dependsOnMethods = "tc03_searchContact",
            description = "TC-04: Update user 1 with profile picture and new data")
    public void tc04_updateContactWithPicture() {
        step("Tap Edit (pencil)", () -> {
            formPage = detailPage.tapEdit();
        });
        step("Add profile picture: Add picture > Gallery > Recent > Choose", () -> {
            formPage.addPictureFromGallery(PROFILE_PICTURE_NAME);
            attachScreenshot("Edit contact with picture");
        });
        step("Update fields to " + UPDATED.fullName() + ", " + UPDATED.company()
                + ", " + UPDATED.phone() + ", " + UPDATED.email(), () -> {
            formPage.updateContact(CONTACT_TO_UPDATE, UPDATED);
        });
        step("Save", () -> {
            detailPage = formPage.save();
        });
        step("Verify updated data on detail page", () -> {
            attachScreenshot("Updated contact detail");
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(detailPage.isTextDisplayed(UPDATED.fullName()),
                    "New full name should be displayed");
            softAssert.assertTrue(detailPage.isTextDisplayed(UPDATED.company()),
                    "New company should be displayed");
            softAssert.assertTrue(detailPage.isPhoneDisplayed(UPDATED.phone()),
                    "New phone should be displayed");
            softAssert.assertTrue(detailPage.isTextDisplayed(UPDATED.email()),
                    "New email should be displayed");
            softAssert.assertTrue(detailPage.isTextNotDisplayed(CONTACT_TO_UPDATE.fullName()),
                    "Old name '" + CONTACT_TO_UPDATE.fullName() + "' should no longer be displayed");
            softAssert.assertAll();
        });
    }

    // ===================== DELETE =====================

    @Test(priority = 5, groups = "Delete", dependsOnMethods = "tc04_updateContactWithPicture",
            description = "TC-05: Delete all contacts and verify the list is empty")
    public void tc05_deleteAllContacts() {
        step("Back to the full contacts list", () -> {
            listPage = detailPage.returnToContactsList();
        });
        step("Verify the 3 contacts before delete", () -> {
            SoftAssert softAssert = new SoftAssert();
            for (String name : CONTACTS_TO_DELETE) {
                softAssert.assertTrue(listPage.isContactDisplayed(name),
                        name + " should be in contacts list before delete");
            }
            softAssert.assertAll();
        });
        step("Select the 3 contacts (long press + tap)", () -> {
            listPage.selectContacts(CONTACTS_TO_DELETE);
            attachScreenshot("3 contacts selected");
            Assert.assertTrue(listPage.isSelectedCountDisplayed(CONTACTS_TO_DELETE.size()),
                    "'" + CONTACTS_TO_DELETE.size() + " selected' should be displayed");
        });
        step("Delete selected contacts (trash > Delete)", () -> {
            listPage.deleteSelectedContacts();
        });
        step("Verify 'No contacts yet'", () -> {
            attachScreenshot("Empty contacts list");
            Assert.assertTrue(listPage.isNoContactsMessageDisplayed(),
                    "'No contacts yet' should be displayed after deleting all contacts");
        });
    }

    // ===================== helpers =====================

    // Create -> verify detail (5 assertions) -> back to list -> verify in list
    private void createAndVerify(Contact contact) {
        step("Create contact: " + contact.fullName(), () -> {
            detailPage = listPage
                    .tapAddContact()
                    .fillContactForm(contact)
                    .save();
        });
        step("Verify detail of " + contact.fullName()
                + " (name, company, phone, email, creation date)", () -> {
            attachScreenshot("Detail of " + contact.fullName());
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(detailPage.isTextDisplayed(contact.fullName()),
                    "Full name should be displayed");
            softAssert.assertTrue(detailPage.isTextDisplayed(contact.company()),
                    "Company should be displayed");
            softAssert.assertTrue(detailPage.isPhoneDisplayed(contact.phone()),
                    "Phone should be displayed");
            softAssert.assertTrue(detailPage.isTextDisplayed(contact.email()),
                    "Email should be displayed");
            softAssert.assertTrue(detailPage.isAddedToday(),
                    "Today's creation date should be displayed");
            softAssert.assertAll();
        });
        step("Back to list and verify " + contact.fullName(), () -> {
            listPage = detailPage.goBackToList();
            Assert.assertTrue(listPage.isContactDisplayed(contact.fullName()),
                    contact.fullName() + " should be displayed in contacts list");
        });
    }
}