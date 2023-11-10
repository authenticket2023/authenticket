package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.Ticket;
import com.itextpdf.text.*;
import org.springframework.core.io.InputStreamResource;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

public interface PDFGenerator {
    InputStreamResource generateOrderDetails(Order order) throws DocumentException, FileNotFoundException;
    InputStreamResource generateTicketQRCode(Ticket ticket, LocalDateTime expirationDate) throws DocumentException, FileNotFoundException;
}
