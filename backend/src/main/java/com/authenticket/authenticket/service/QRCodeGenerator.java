package com.authenticket.authenticket.service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface QRCodeGenerator {

    byte[] getQRCode(String text, int width, int height) throws WriterException, IOException;
}
