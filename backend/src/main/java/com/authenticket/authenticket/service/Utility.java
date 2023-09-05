package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.GeneralApiResponse;
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

    public String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public GeneralApiResponse<Object> generateApiResponse(Object data, String message) {
        if (data == null) {
            return GeneralApiResponse.builder()
                    .message(message)
                    .build();
        } else {
            return GeneralApiResponse.builder()
                    .message(message)
                    .data(data)
                    .build();
        }
    }
}
