package com.test.contacts.utils;

import com.aventstack.extentreports.ExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentTestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(ExtentTestListener.class);
    private static final String EXTENT_TEST = "extentTest";

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentManager.getInstance().createTest(testName(result));
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            test.assignCategory(groups);
        }
        result.setAttribute(EXTENT_TEST, test);
        ExtentManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test(result).pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = test(result);
        ReportHelper.attachScreenshot("Screen at failure");
        test.fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Skipped because a previous test failed";
        test(result).skip(reason);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
        log.info("Extent report generated: {}", ExtentManager.reportLocation());
    }

    // Skipped tests (dependency failed) never call onTestStart, so the entry is created here
    private ExtentTest test(ITestResult result) {
        Object existing = result.getAttribute(EXTENT_TEST);
        if (existing instanceof ExtentTest test) {
            return test;
        }
        ExtentTest test = ExtentManager.getInstance().createTest(testName(result));
        result.setAttribute(EXTENT_TEST, test);
        return test;
    }

    private String testName(ITestResult result) {
        String description = result.getMethod().getDescription();
        return (description == null || description.isBlank())
                ? result.getMethod().getMethodName()
                : description;
    }
}