package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.exception.NotApprovedException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Utility {
    @Autowired
    private EventRepository eventRepository;


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

    public void checkIfEventExistsAndIsApprovedAndNotDeleted(Integer eventId){
        Event event = eventRepository.findById(eventId).orElse(null);
        if(event == null){
            throw new NonExistentException("Event",eventId);
        } else if(event.getDeletedAt()!=null){
            throw new AlreadyDeletedException("Event", eventId);
        } else if(!Objects.equals(event.getReviewStatus(), "approved")){
            throw new NotApprovedException("Event",eventId);
        };
    }
}
