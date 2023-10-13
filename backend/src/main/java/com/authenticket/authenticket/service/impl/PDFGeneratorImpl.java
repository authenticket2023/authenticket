package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.service.JwtService;
import com.authenticket.authenticket.service.PDFGenerator;
import com.authenticket.authenticket.service.QRCodeGenerator;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.NumberFormat;
import java.util.TreeSet;

@Service
public class PDFGeneratorImpl implements PDFGenerator {

    private final QRCodeGenerator qrCodeGenerator;

    private final JwtService jwtService;

    private final String iconPath = new ClassPathResource("src\\main\\resources\\static\\img\\icon.png").getPath();

    @Autowired
    public PDFGeneratorImpl(QRCodeGenerator qrCodeGenerator, JwtService jwtService) {
        this.qrCodeGenerator = qrCodeGenerator;
        this.jwtService = jwtService;
    }

    @Override
    public InputStreamResource generateOrderDetails(Order order) {
        try {
            int ticketCount = 0;
            Event event = null;
            TicketPricing ticketPricing = null;
            for (Ticket t : order.getTicketSet()) {
                ticketCount++;
                ticketPricing = t.getTicketPricing();
                event = t.getTicketPricing().getEvent();
            }
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable table = new PdfPTable(2); // Create 2 columns in table.
            // Set table Width as 100%
            table.setWidthPercentage(100f);
            float[] columnWidths = { 1f, 1f }; // Second column will be
            // twice as first and third
            table.setWidths(columnWidths);
            Image image = Image.getInstance(iconPath);
            image.scalePercent(15f);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(image);
            table.addCell(cell);

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(24);
            fontTitle.setColor(BaseColor.GRAY);
            Paragraph title = new Paragraph("Order Details", fontTitle);
            title.setAlignment(Element.ALIGN_RIGHT);
            title.setIndentationRight(15f);
            cell = new PdfPCell();
            cell.setPaddingTop(10f);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(title);
            table.addCell(cell);
            document.add(table);

            table = new PdfPTable(2); // Create 2 columns in table.
            // Set table Width as 100%
            table.setWidthPercentage(100f);
            // Space before and after table
            table.setSpacingBefore(15f);
            table.setSpacingAfter(15f);
            // Set Column widths of table
            columnWidths[1] = 1;
            // twice as first and third
            table.setWidths(columnWidths);

            Font pFontBold =  FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            pFontBold.setColor(BaseColor.BLACK);
            pFontBold.setSize(14);
            Font hFontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            hFontBold.setColor(BaseColor.LIGHT_GRAY);
            hFontBold.setSize(16);

            Font pFont =  FontFactory.getFont(FontFactory.HELVETICA);
            pFont.setSize(14);
            PdfPCell cell1 = new PdfPCell(new Paragraph("Purchased By", hFontBold));
            cell1.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            PdfPCell cell2 = new PdfPCell(new Paragraph("Event Details", hFontBold));
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);

            cell1 = new PdfPCell();
            cell1.setPaddingRight(15f);
            cell1.setBorder(Rectangle.NO_BORDER);
            Paragraph p = new Paragraph();
            p.add(new Phrase("Buyer Name: ", pFontBold));
            p.add(new Phrase(order.getUser().getName(), pFont));
            cell1.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Buyer Email: ", pFontBold));
            p.add(new Phrase(order.getUser().getEmail(), pFont));
            cell1.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Order ID: ", pFontBold));
            p.add(new Phrase(String.valueOf(order.getOrderId()), pFont));
            cell1.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Order Time: ", pFontBold));
            p.add(new Phrase(order.getPurchaseDate().toString(), pFont));
            cell1.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Order Status: ", pFontBold));
            p.add(new Phrase(order.getOrderStatus(), pFont));
            cell1.addElement(p);

            p = new Paragraph();
            p.add(new Phrase("Event Name: ", pFontBold));
            p.add(new Phrase(event.getEventName(), pFont));
            p.setPaddingTop(30f);
            cell2.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Event Location: ", pFontBold));
            p.add(new Phrase(event.getVenue().getVenueName() + ", " + event.getVenue().getVenueLocation(), pFont));
            cell2.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Event Date: ", pFontBold));
            p.add(new Phrase(event.getEventDate().toString(), pFont));
            cell2.addElement(p);
            p = new Paragraph();
            p.add(new Phrase("Event Type: ", pFontBold));
            p.add(new Phrase(event.getEventType().getEventTypeName(), pFont));
            cell2.addElement(p);

            table.addCell(cell1);
            table.addCell(cell2);
            document.add(table);
            document.add(new LineSeparator());

            // Ticket price table

            fontTitle.setColor(BaseColor.BLACK);
            fontTitle.setSize(16);
            title = new Paragraph("Ticket Summary", fontTitle);
            title.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(title);

            table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(20f);
            float[] columnWidths2 = { 5f, 1f, 1f, 1f };
            table.setWidths(columnWidths2);

            cell1 = new PdfPCell(new Paragraph("Ticket", pFontBold));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2 = new PdfPCell(new Paragraph("Quantity", pFontBold));
            cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            PdfPCell cell3 = new PdfPCell(new Paragraph("Price", pFontBold));
            cell3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell3.setBorder(Rectangle.NO_BORDER);
            PdfPCell cell4 = new PdfPCell(new Paragraph("Subtotal", pFontBold));
            cell4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            document.add(table);
            document.add(new LineSeparator());

            table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            table.setWidths(columnWidths2);
            cell1 = new PdfPCell(new Paragraph(event.getEventName() + ": " + ticketPricing.getCat().getCategoryName(), pFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell2 = new PdfPCell(new Paragraph(String.valueOf(ticketCount), pFont));
            cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);

            NumberFormat priceFormat = NumberFormat.getCurrencyInstance();
            cell3 = new PdfPCell(new Paragraph(priceFormat.format(ticketPricing.getPrice()), pFont));
            cell3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell4 = new PdfPCell(new Paragraph(priceFormat.format(ticketPricing.getPrice() * ticketCount), pFont));
            cell4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            document.add(table);

            document.add(new LineSeparator());
            table = new PdfPTable(1);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            cell1 = new PdfPCell();
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            p = new Paragraph();
            p.setAlignment(Element.ALIGN_RIGHT);
            p.add(new Phrase("Order Total: ", pFontBold));
            p.add(new Phrase(priceFormat.format(order.getOrderAmount()), pFont));
            p.setIndentationRight(9f);
            cell1.addElement(p);
            table.addCell(cell1);
            document.add(table);
            document.add(new LineSeparator());


            table = new PdfPTable(5);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            float[] columnWidths3 = { 3f, 1f, 1f, 1f, 1f };
            table.setWidths(columnWidths3);
            cell1 = new PdfPCell();
            cell1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.addElement(new Paragraph("Ticket Holder", pFontBold));
            cell2 = new PdfPCell();
            cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            p = new Paragraph(new Paragraph("Ticket ID", pFontBold));
            p.setAlignment(Element.ALIGN_CENTER);
            cell2.addElement(p);
            cell3 = new PdfPCell();
            cell3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell3.setBorder(Rectangle.NO_BORDER);
            p = new Paragraph(new Paragraph("Section", pFontBold));
            p.setAlignment(Element.ALIGN_CENTER);
            cell3.addElement(p);
            cell4 = new PdfPCell();
            cell4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            p = new Paragraph(new Paragraph("Row", pFontBold));
            p.setAlignment(Element.ALIGN_CENTER);
            cell4.addElement(p);
            PdfPCell cell5 = new PdfPCell();
            cell5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell5.setBorder(Rectangle.NO_BORDER);
            p = new Paragraph(new Paragraph("Seat", pFontBold));
            p.setAlignment(Element.ALIGN_CENTER);
            cell5.addElement(p);

            TreeSet<Ticket> treeSet = new TreeSet<>(order.getTicketSet());
            for (Ticket ticket : treeSet) {
                cell1.addElement(new Paragraph(ticket.getTicketHolder(), pFont));
                p = new Paragraph(String.valueOf(ticket.getTicketId()), pFont);
                p.setAlignment(Element.ALIGN_CENTER);
                cell2.addElement(p);
                p = new Paragraph(String.valueOf(ticket.getSection().getSectionId()), pFont);
                p.setAlignment(Element.ALIGN_CENTER);
                cell3.addElement(p);
                p = new Paragraph(String.valueOf(ticket.getRowNo()), pFont);
                p.setAlignment(Element.ALIGN_CENTER);
                cell4.addElement(p);
                p = new Paragraph(String.valueOf(ticket.getSeatNo()), pFont);
                p.setAlignment(Element.ALIGN_CENTER);
                cell5.addElement(p);
            }
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            document.add(table);

            document.close();
            out.close();
            ByteArrayInputStream bis = new ByteArrayInputStream
                    (out.toByteArray());
            return new InputStreamResource(bis);
        } catch (DocumentException ex) {
            throw new ApiRequestException("Documentation error occurred: " + ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStreamResource generateTicketQRCode(Ticket ticket) {
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable table = new PdfPTable(2); // Create 2 columns in table.
            // Set table Width as 100%
            table.setWidthPercentage(100f);
            // Space before and after table
            table.setSpacingBefore(5f);
            table.setSpacingAfter(35f);
            // Set Column widths of table
            float[] columnWidths = { 1f, 1.5f }; // Second column will be
            // twice as first and third
            table.setWidths(columnWidths);

            Font pFontBold =  FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            pFontBold.setSize(20);
            Font pFont =  FontFactory.getFont(FontFactory.HELVETICA);
            pFont.setSize(20);
            PdfPCell cell1 = new PdfPCell();
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setMinimumHeight(50f);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);

            Image image = Image.getInstance(iconPath);
            image.scalePercent(25f);
            cell2.setRowspan(2);
            cell2.addElement(image);
            table.addCell(cell2);

            Paragraph p = new Paragraph();
            p.add(new Phrase("Ticket ID: ", pFontBold));
            p.add(new Phrase(String.valueOf(ticket.getTicketId()), pFont));
            p.setAlignment(Element.ALIGN_BASELINE);
            cell1.addElement(p);
            table.addCell(cell1);


            cell1 = new PdfPCell();
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setMinimumHeight(50f);
            p = new Paragraph("Ticket Holder: ", pFontBold);
            cell1.addElement(p);
            p = new Paragraph(ticket.getTicketHolder(), pFont);
            cell1.addElement(p);
            table.addCell(cell1);
            document.add(table);

            p = new Paragraph("Section " + ticket.getSection().getSectionId() + ", Row " + ticket.getRowNo() + ", Seat " + ticket.getSeatNo());
            p.setSpacingAfter(-25f);
            p.setIndentationLeft(30f);
            document.add(p);

            image = Image.getInstance(qrCodeGenerator.getQRCode(jwtService.generateToken(ticket),350, 300));
            image.scaleAbsolute(300, 300);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);
            document.close();
            out.close();
            ByteArrayInputStream bis = new ByteArrayInputStream
                    (out.toByteArray());
            return new InputStreamResource(bis);
        } catch (DocumentException ex) {
            throw new ApiRequestException("Documentation error occurred: " + ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
