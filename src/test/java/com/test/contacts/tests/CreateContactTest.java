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

    // Created in this order, all in the same session
    private static final List<Contact> CONTACTS = List.of(
            new Contact("David", "Soliz", "AssureSoft", "70708406", "soliz.g.david@gmail.com"),
            new Contact("Joseph", "Colbert", "Unosquare", "71234567", "joseph.colbert@example.com"),
            new Contact("Oscar", "Segales", "Jalasoft", "76543210", "oscar.segales@example.com")
    );

    @Test(description = "TC-01: Create 3 contacts in one session and verify detail and list")
    public void shouldCreateThreeContacts() {
        SoftAssert softAssert = new SoftAssert();

        // Home -> swipe up -> Contacts (only once)
        ContactsListPage listPage = new HomePage().openContactsApp();

        for (int i = 0; i < CONTACTS.size(); i++) {
            Contact contact = CONTACTS.get(i);
            log.info("========== User {}: {} ==========", i + 1, contact.fullName());

            // Create
            ContactDetailPage detailPage = listPage
                    .tapAddContact()
                    .fillContactForm(contact)
                    .save();

            // Assertions on detail page
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

            // Back to the list and verify the contact is there
            listPage = detailPage.goBackToList();
            softAssert.assertTrue(listPage.isContactDisplayed(contact.fullName()),
                    contact.fullName() + ": should be displayed in contacts list");

            log.info("RESULT: '{}' created and verified (detail + list)", contact.fullName());
        }

        // Final assertion: all 3 contacts are in the list
        log.info("========== Final check: all contacts in list ==========");
        for (Contact contact : CONTACTS) {
            softAssert.assertTrue(listPage.isContactDisplayed(contact.fullName()),
                    "Final check: " + contact.fullName() + " should be in contacts list");
        }

        softAssert.assertAll();
        log.info("RESULT: All {} contacts were created successfully", CONTACTS.size());
    }
}