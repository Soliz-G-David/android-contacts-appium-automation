package com.test.contacts.utils;

import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class DeviceFileHelper {

    private static final Logger log = LoggerFactory.getLogger(DeviceFileHelper.class);
    private static final String DEVICE_PICTURES_DIR = "/sdcard/Pictures/";

    private DeviceFileHelper() {
    }

    // Copies a local image to the emulator and registers it in the gallery
    public static String pushImageToGallery(String localPath) {
        File image = new File(localPath);
        if (!image.exists()) {
            throw new IllegalStateException("Image not found: " + image.getAbsolutePath());
        }

        AndroidDriver driver = DriverManager.getDriver();
        String remotePath = DEVICE_PICTURES_DIR + image.getName();

        try {
            driver.pushFile(remotePath, image);
            log.info("Image pushed to device: {}", remotePath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not push image to device", e);
        }

        try {
            driver.executeScript("mobile: scanMedia", Map.of("remotePath", remotePath));
            log.info("Media scan done, image available in gallery");
        } catch (Exception e) {
            log.info("Media scan not available: {}", e.getMessage());
        }
        return remotePath;
    }
}