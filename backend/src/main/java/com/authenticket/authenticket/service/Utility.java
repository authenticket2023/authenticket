package com.authenticket.authenticket.service;

import org.apache.commons.lang3.RandomStringUtils;

public class Utility {
    public String getFileExtension(String contentType) {
        if (contentType == null) {
            return null;
        }
        if (contentType.equals("image/jpeg")) {
            return ".jpg";
        } else if (contentType.equals("image/png")) {
            return ".png";
        } else if (contentType.equals("image/gif")) {
            return ".gif";
        } // Add more cases for other supported file types
        return null; // Unsupported file type
    }

    protected String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }
}
