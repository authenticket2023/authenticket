package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.service.QRCodeGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QRCodeGeneratorImplTest {
    @Mock
    private QRCodeWriter qrCodeWriter;

    private QRCodeGeneratorImpl underTest;

    @BeforeEach
    void setUp(){
        underTest = new QRCodeGeneratorImpl();
    }
    @Test
    public void testGetQRCode() throws WriterException, IOException {
        // Mock data
        String text = "Test QR Code";
        int width = 200;
        int height = 200;

        // Test the method
        byte[] qrCodeData = underTest.getQRCode(text, width, height);

        // Assertions
        assertNotNull(qrCodeData); // Replace with your actual expected byte array
    }
}