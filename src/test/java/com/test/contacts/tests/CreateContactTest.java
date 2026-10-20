package com.test.contacts.tests;

import com.test.contacts.base.BaseTest;
import com.test.contacts.models.Contact;
import com.test.contacts.pages.ContactDetailPage;
import com.test.contacts.pages.ContactsListPage;
import com.test.contacts.pages.HomePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class CreateContactTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(CreateContactTest.class);

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

    @Test(description = "Contacts flow: create 3 contacts, then search and update user 1")
    public void shouldCreateAndUpdateContacts() {
        SoftAssert softAssert = new SoftAssert();

        // Home -> swipe up -> Contacts (only once)
        ContactsListPage listPage = new HomePage().openContactsApp();

        // ===== CREATE: users 1, 2 and 3 =====
        for (int i = 0; i < CONTACTS.size(); i++) {
            Contact contact = CONTACTS.get(i);
            log.info("========== CREATE user {}: {} ==========", i + 1, contact.fullName());

            ContactDetailPage detailPage = listPage
                    .tapAddContact()
                    .fillContactForm(contact)
                    .save();

            softAssert.assertTrue(detailPage.isTextDisplayed(contact.fullName()),
                    contact.fullName() + ": full name should be displayed on detail page");
            softAssert.assertTrue(detailPage.isTextDisplayed(contact.company()),
                    contact.fullName() + ": company should be displayed on detail page");
            softAssert.assertTrue(detailPage.isPhoneDisplayed(contact.phone()),
                    contact.fullName() + ": phone should be displayed on detail page");
            softAssert.assertTrue(detailPage.isTextDisplayed(contact.email()),
                    contact.fullName() + ": email should be displayed on detail page");
            softAssert.assertTrue(detailPage.isAddedToday(),
                    contact.fullName() + ": should show today's creation date");

            listPage = detailPage.goBackToList();
            softAssert.assertTrue(listPage.isContactDisplayed(contact.fullName()),
                    contact.fullName() + ": should be displayed in contacts list");

            log.info("RESULT: '{}' created and verified (detail + list)", contact.fullName());
        }

        log.info("========== CHECK: all contacts in list ==========");
        for (Contact contact : CONTACTS) {
            softAssert.assertTrue(listPage.isContactDisplayed(contact.fullName()),
                    "List check: " + contact.fullName() + " should be in contacts list");
        }

        // ===== SEARCH and UPDATE: user 1 =====
        log.info("========== SEARCH '{}' and UPDATE ==========", SEARCH_TEXT);

        ContactDetailPage detailPage = listPage
                .searchAndOpenContact(SEARCH_TEXT, CONTACT_TO_UPDATE.fullName());

        softAssert.assertTrue(detailPage.isTextDisplayed(CONTACT_TO_UPDATE.fullName()),
                "Search should open '" + CONTACT_TO_UPDATE.fullName() + "'");

        detailPage = detailPage
                .tapEdit()
                .updateContact(CONTACT_TO_UPDATE, UPDATED)
                .save();

        // Final assertions: updated data on detail page
        softAssert.assertTrue(detailPage.isTextDisplayed(UPDATED.fullName()),
                "Update: new full name should be displayed");
        softAssert.assertTrue(detailPage.isTextDisplayed(UPDATED.company()),
                "Update: new company should be displayed");
        softAssert.assertTrue(detailPage.isPhoneDisplayed(UPDATED.phone()),
                "Update: new phone should be displayed");
        softAssert.assertTrue(detailPage.isTextDisplayed(UPDATED.email()),
                "Update: new email should be displayed");
        softAssert.assertTrue(detailPage.isTextNotDisplayed(CONTACT_TO_UPDATE.fullName()),
                "Update: old name '" + CONTACT_TO_UPDATE.fullName() + "' should no longer be displayed");

        softAssert.assertAll();

        log.info("RESULT: {} contacts created and '{}' updated to '{}'",
                CONTACTS.size(), CONTACT_TO_UPDATE.fullName(), UPDATED.fullName());
    }
}