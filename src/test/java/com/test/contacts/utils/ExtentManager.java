package com.test.contacts.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExtentManager {

    private static final Path REPORT_PATH = Path.of("reports", "ContactsCrudReport.html");
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();
    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            try {
                Files.createDirectories(REPORT_PATH.getParent());
            } catch (IOException e) {
                throw new IllegalStateException("Could not create reports folder", e);
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH.toString());
            spark.config().setDocumentTitle("Android Contacts CRUD - Test Report");
            spark.config().setReportName("Android Contacts CRUD | Appium + TestNG");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Device", "Pixel 7 emulator - Android 14 (API 34)");
            extent.setSystemInfo("App under test", "Google Contacts");
            extent.setSystemInfo("Framework", "Appium + UiAutomator2 + TestNG + POM");
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return CURRENT_TEST.get();
    }

    static void setTest(ExtentTest test) {
        CURRENT_TEST.set(test);
    }

    public static String reportLocation() {
        return REPORT_PATH.toAbsolutePath().toUri().toString();
    }
}