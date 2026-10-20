package com.test.contacts.tests;

import com.test.contacts.base.BaseTest;
import com.test.contacts.models.Contact;
import com.test.contacts.pages.ContactDetailPage;
import com.test.contacts.pages.HomePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class CreateContactTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(CreateContactTest.class);

    private static final Contact NEW_CONTACT = new Contact(
            "David",
            "Soliz",
            "AssureSoft",
            "70708406",
            "soliz.g.david@gmail.com"
    );

    @Test(description = "TC-01: Create a new contact with name, company, phone and email")
    public void shouldCreateNewContact() {
        ContactDetailPage detailPage = new HomePage()
                .openContactsApp()
                .tapAddContact()
                .fillContactForm(NEW_CONTACT)
                .save();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(detailPage.isTextDisplayed(NEW_CONTACT.fullName()),
                "Full name should be displayed on detail page");
        softAssert.assertTrue(detailPage.isTextDisplayed(NEW_CONTACT.company()),
                "Company should be displayed on detail page");
        softAssert.assertTrue(detailPage.isPhoneDisplayed(NEW_CONTACT.phone()),
                "Phone should be displayed on detail page");
        softAssert.assertTrue(detailPage.isTextDisplayed(NEW_CONTACT.email()),
                "Email should be displayed on detail page");
        softAssert.assertTrue(detailPage.isAddedToday(),
                "Contact should show today's creation date ('Added <today>')");

        softAssert.assertAll();

        log.info("RESULT: Contact '{}' was created successfully "
                + "(name, company, phone, email and creation date verified)", NEW_CONTACT.fullName());
    }
}