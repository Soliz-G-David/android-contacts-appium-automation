# Android Contacts CRUD – Appium Mobile Test Automation

End-to-end mobile test automation framework for the **Google Contacts** app on Android.
It covers the full **CRUD** flow (Create, Read/Search, Update, Delete) using **Appium**, **Java**, **TestNG** and the **Page Object Model**, and generates an **HTML report** with charts and screenshots.

---

## Tech stack

| Tool                   | Version                                    | Purpose                                 |
| ---------------------- | ------------------------------------------ | --------------------------------------- |
| Java (Eclipse Temurin) | 21                                         | Programming language                    |
| Gradle (Wrapper)       | 9.7                                        | Build and dependency management         |
| Appium Server          | 3.x                                        | Mobile automation server                |
| UiAutomator2 driver    | 8.x                                        | Android automation engine               |
| Appium Java Client     | 10.0.0                                     | Appium bindings for Java (Selenium 4)   |
| TestNG                 | 7.11.0                                     | Test runner, assertions, suite          |
| ExtentReports          | 5.1.2                                      | HTML report with charts and screenshots |
| SLF4J Simple           | 2.0.17                                     | Step logs in the console                |
| Android Emulator       | Pixel 7 – Android 14 (API 34, Google APIs) | Device under test                       |

---

## Test scenarios

All scenarios run **in one Appium session** as a continuous flow. Each test depends on the previous one: if a test fails, the next ones are marked as **skipped**.

| ID    | Category | Scenario                                                                      | Main assertions                                                                                         |
| ----- | -------- | ----------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| TC-01 | Create   | Create user 1 (David Soliz)                                                   | Name, company, phone, email and today's creation date on the detail page; contact displayed in the list |
| TC-02 | Create   | Create users 2 and 3 (Joseph Colbert, Oscar Segales)                          | Same assertions for each user; all 3 contacts displayed in the list                                     |
| TC-03 | Search   | Search by last name **"Soliz"** and open the contact                          | The right contact is opened                                                                             |
| TC-04 | Update   | Edit user 1: add a **profile picture** from the gallery and update all fields | New name, company, phone and email displayed; old name no longer displayed                              |
| TC-05 | Delete   | Select the 3 contacts (long press) and delete them                            | "3 selected" displayed; **"No contacts yet"** displayed after deleting                                  |

### Test data

| User        | First name | Last name | Company    | Phone     | Email                      |
| ----------- | ---------- | --------- | ---------- | --------- | -------------------------- |
| 1           | David      | Soliz     | AssureSoft | 70708406  | soliz.g.david@gmail.com    |
| 2           | Joseph     | Colbert   | Unosquare  | 71234567  | joseph.colbert@example.com |
| 3           | Oscar      | Segales   | Jalasoft   | 76543210  | oscar.segales@example.com  |
| 1 (updated) | Davichin   | Gallagher | CodeRoad   | 777777777 | davichin90s@gmail.com      |

---

## Project structure

```
android-contacts-appium-automation
├── build.gradle                     # Dependencies and test task (TestNG suite)
├── src/test
│   ├── java/com/test/contacts
│   │   ├── base
│   │   │   └── BaseTest.java              # Opens/closes the Appium session (one per class)
│   │   ├── models
│   │   │   └── Contact.java               # Test data model (record)
│   │   ├── pages                          # Page Object Model
│   │   │   ├── BasePage.java              # Waits, gestures, scroll, keyboard, locator helpers
│   │   │   ├── HomePage.java              # Home > swipe up > Contacts
│   │   │   ├── ContactsListPage.java      # Add, search, select and delete contacts
│   │   │   ├── ContactFormPage.java       # Create/edit form, profile picture
│   │   │   └── ContactDetailPage.java     # Detail assertions, edit, back navigation
│   │   ├── tests
│   │   │   └── CreateContactTest.java     # CRUD flow: TC-01 to TC-05
│   │   └── utils
│   │       ├── ConfigReader.java          # Reads config.properties
│   │       ├── DriverManager.java         # AndroidDriver (ThreadLocal)
│   │       ├── DeviceFileHelper.java      # Pushes the profile picture to the emulator
│   │       ├── ExtentManager.java         # ExtentReports configuration
│   │       ├── ExtentTestListener.java    # TestNG listener: results and screenshots
│   │       └── ReportHelper.java          # Report steps and screenshots
│   └── resources
│       ├── config.properties              # Appium URL, device, timeouts
│       ├── simplelogger.properties        # Log format
│       ├── testng.xml                     # Test suite and listener
│       └── images/profile.jpg             # Profile picture used in TC-04
└── reports/ContactsCrudReport.html        # Generated after each run (not versioned)
```

---

## Framework design

- **Page Object Model with fluent interface:** each action returns the next page, so the test reads like the user flow.
- **BasePage:** explicit waits (`WebDriverWait`), W3C gestures (swipe, tap), scroll and keyboard handling are written once and reused by every page. No `Thread.sleep`.
- **Resilient locators:** elements are found by visible text, hint or content description, with fallbacks, instead of internal resource IDs that change between app versions.
- **Soft assertions:** all checks of a step run, and every failure is reported together.
- **Locale-safe phone assertion:** the phone is compared by digits only, because Android formats the number on screen.
- **External configuration:** `config.properties` keeps the Appium URL, device and timeouts out of the code.
- **Self-contained test data:** the profile picture is copied to the emulator gallery at the start of the run (`pushFile` + media scan).
- **Clean state:** TC-05 deletes all created contacts, so the suite can run again without manual cleanup.
- **Failure diagnostics:** on failure, a screenshot is attached to the report; the picture step also saves the UI hierarchy in `build/diagnostics`.

---

## Prerequisites

1. **JDK 21** with `JAVA_HOME` pointing to it.
2. **Android Studio** with an emulator: **Pixel 7, API 34, Google APIs** (no Play Store image), and `ANDROID_HOME` pointing to the Android SDK.
3. **Node.js 20+**.
4. **Appium 3** and the **UiAutomator2** driver:

```bash
   npm install -g appium
   appium driver install uiautomator2
```

5. Check the environment:

```bash
   java -version
   adb devices
   appium driver doctor uiautomator2
```

---

## How to run

1. Start the emulator from Android Studio (Device Manager) and confirm it is connected:

```bash
   adb devices
```

2. Start the Appium server in a separate terminal and keep it open:

```bash
   appium
```

3. Run the suite from the project root:

```bash
   ./gradlew clean test
```

On Windows PowerShell use `.\gradlew clean test`.

> The emulator should start with an empty contacts list. TC-05 deletes every contact created by the suite, so it stays clean for the next run.

---

## Test report

After each run, an HTML report is generated at:

```
reports/ContactsCrudReport.html
```

Open it with a double click (Chrome or Edge), or from Git Bash:

```bash
start reports/ContactsCrudReport.html
```

The report includes:

- **Dashboard** with pie charts of tests and steps (passed, failed, skipped), start/end time, duration and device information.
- **Each test case (TC-01 to TC-05)** with its steps marked as PASS/FAIL.
- **Emulator screenshots** embedded in the HTML (detail pages, selected contacts, empty list).
- **Categories:** Create, Search, Update and Delete.

The report is a single file, so it can be shared as it is.

---

## Commit history

| #   | Commit                                                                                                                         | Description                                                                          |
| --- | ------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------ |
| 1   | [fa40897](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/fa40897cf7af7c81b4ae4cfffdee02fbfc513bc3) | Initial project setup with Gradle, Appium and TestNG                                 |
| 2   | [7b5e61a](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/7b5e61a424fc11efc589d7103ed2156cfd3bdd25) | Create contact (TC-01) with POM, assertions and step logs                            |
| 3   | [ca507bb](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/ca507bbec347341734209420600da2a336e20447) | Add users 2 and 3 to the create flow in one session; verify each contact in the list |
| 4   | [9f3afe5](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/9f3afe58b67179c472b8536beb0c4c8494a01ffe) | Search contact by last name and update all its data                                  |
| 5   | [2d8a7e5](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/2d8a7e5f5f0284b2b2ced32d0d6cbcb3fafab1db) | Add profile picture from the gallery before updating the contact                     |
| 6   | [78e5576](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/78e55767da0b88e7e515df73f533830dc495606c) | Delete all contacts to complete the CRUD flow                                        |
| 7   | [fdb0ff0](https://github.com/Soliz-G-David/android-contacts-appium-automation/commit/fdb0ff0e24ce2021eb77edf49e65b4f61325a43e) | ExtentReports HTML report; CRUD flow split into 5 chained test cases                 |

---

## Author

**David Soliz Garcia** – QA Automation Engineer
GitHub: [Soliz-G-David](https://github.com/Soliz-G-David)
