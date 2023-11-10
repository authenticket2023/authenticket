package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.service.QRCodeGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Implementation of the QRCodeGenerator interface that generates QR codes as byte arrays.
 */
@Service
public class QRCodeGeneratorImpl implements QRCodeGenerator {
    /**
     * Generates a QR code as a byte array from the given text with the specified width and height.
     *
     * @param text   The text or content to encode in the QR code.
     * @param width  The desired width of the QR code image.
     * @param height The desired height of the QR code image.
     * @return A byte array containing the QR code image in PNG format.
     * @throws WriterException If an error occurs during QR code generation.
     * @throws IOException    If an error occurs while writing the QR code image to a byte array.
     */
    @Override
    public byte[] getQRCode(String text, int width, int height) throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter
                .encode(text, BarcodeFormat.QR_CODE,
                        width, height);
        ByteArrayOutputStream pngOutputStream =
                new ByteArrayOutputStream();
        MatrixToImageConfig con = new MatrixToImageConfig( 0xFF000002 , 0xFFFFC041 ) ;
//        MatrixToImageConfig con =
//                new MatrixToImageConfig(0xFF000002, 0xFF04B4AE);
        MatrixToImageWriter.writeToStream
                (bitMatrix, "PNG", pngOutputStream, con);
        byte[] pngData = pngOutputStream.toByteArray();

        return pngData;
    }
}
