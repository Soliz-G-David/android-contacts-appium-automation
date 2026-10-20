package com.test.contacts.base;

import com.test.contacts.utils.DriverManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    // One Appium session shared by all tests of the class (continuous flow)
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        DriverManager.createDriver();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}