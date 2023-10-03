package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.Ticket;
import com.itextpdf.text.*;
import org.springframework.core.io.InputStreamResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public interface PDFGenerator {
    InputStreamResource generateOrderDetails(Order order) throws DocumentException, FileNotFoundException;
    InputStreamResource generateTicketQRCode(Ticket ticket) throws DocumentException, FileNotFoundException;
}
