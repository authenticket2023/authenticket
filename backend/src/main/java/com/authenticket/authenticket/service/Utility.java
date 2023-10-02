package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
