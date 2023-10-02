package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.service.PDFGenerator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.Set;
import java.util.TreeSet;

@Service
public class PDFGeneratorImpl implements PDFGenerator {

    public InputStreamResource InputStreamResource(byte[] pngData)
            throws DocumentException,
            MalformedURLException, IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font font = FontFactory
                .getFont(FontFactory.COURIER,
                        14, BaseColor.BLACK);
        Paragraph para = new Paragraph("Scan QR Code", font);
        para.setAlignment(Element.ALIGN_CENTER);
        document.add(para);
        document.add(Chunk.NEWLINE);

        Image image = Image.getInstance(pngData);
        image.scaleAbsolute(170f, 170f);
        image.setAlignment(Element.ALIGN_CENTER);

        document.add(image);
        document.close();
        ByteArrayInputStream bis = new ByteArrayInputStream
                (out.toByteArray());
        return new InputStreamResource(bis);
    }

    public InputStreamResource  generateOrderDetails(Order order) throws DocumentException, FileNotFoundException {
        try {
            int ticketCount = 0;
            Event event = null;
            TicketPricing ticketPricing = null;
            for (Ticket t : order.getTicketSet()) {
                ticketCount++;
                ticketPricing = t.getTicketPricing();
                event = t.getTicketPricing().getEvent();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileOutputStream fos = new FileOutputStream(".\\Folder");
            Document document = new Document(PageSize.A4);
//            PdfWriter.getInstance(document, out);
            PdfWriter.getInstance(document, fos);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(24);
            fontTitle.setColor(BaseColor.GRAY);
            Paragraph title = new Paragraph("Order Details", fontTitle);
            title.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2); // Create 2 columns in table.
            // Set table Width as 100%
            table.setWidthPercentage(100f);
            // Space before and after table
            table.setSpacingBefore(5f);
            table.setSpacingAfter(20f);
            // Set Column widths of table
            float[] columnWidths = { 1f, 1f }; // Second column will be
            // twice as first and third
            table.setWidths(columnWidths);

            Font pFontBold =  FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            pFontBold.setSize(14);
            Font pFont =  FontFactory.getFont(FontFactory.HELVETICA);
            pFont.setSize(14);
            PdfPCell cell1 = new PdfPCell();
            cell1.setBorder(Rectangle.NO_BORDER);
            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);

            cell1.addElement(new Paragraph("Purchased By", pFontBold));
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

            order.getOrderAmount();
            cell2.addElement(new Paragraph("Event Details", pFontBold));
            p = new Paragraph();
            p.add(new Phrase("Event Name: ", pFontBold));
            p.add(new Phrase(event.getEventName(), pFont));
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

//            Paragraph p2 = new Paragraph("This is a para", fontParagraph);
//            p2.setAlignment(Paragraph.ALIGN_LEFT);

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
            p.add(new Phrase(priceFormat.format(ticketPricing.getPrice() * ticketCount), pFont));
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

            TreeSet<Ticket> treeSet = new TreeSet(order.getTicketSet());
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
            fos.close();
            out.close();
            ByteArrayInputStream bis = new ByteArrayInputStream
                    (out.toByteArray());
            return new InputStreamResource(bis);
        } catch (DocumentException ex) {

            System.out.println("Error occurred: " + ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
