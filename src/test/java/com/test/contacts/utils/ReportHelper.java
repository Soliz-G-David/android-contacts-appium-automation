package com.test.contacts.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReportHelper {

    private static final Logger log = LoggerFactory.getLogger(ReportHelper.class);

    @FunctionalInterface
    public interface StepAction {
        void run() throws Exception;
    }

    private ReportHelper() {
    }

    // Runs a step and writes it in the report as PASS or FAIL
    public static void step(String name, StepAction action) {
        ExtentTest test = ExtentManager.getTest();
        try {
            action.run();
            if (test != null) {
                test.pass(name);
            }
        } catch (Throwable t) {
            if (test != null) {
                test.fail("Step failed: " + name);
            }
            if (t instanceof RuntimeException runtime) {
                throw runtime;
            }
            if (t instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException(t);
        }
    }

    // Embeds an emulator screenshot (base64) inside the HTML report
    public static void attachScreenshot(String name) {
        ExtentTest test = ExtentManager.getTest();
        if (test == null) {
            return;
        }
        try {
            String base64 = DriverManager.getDriver().getScreenshotAs(OutputType.BASE64);
            test.info(name, MediaEntityBuilder.createScreenCaptureFromBase64String(base64, name).build());
        } catch (Exception e) {
            log.info("Screenshot not attached: {}", e.getMessage());
        }
    }
}