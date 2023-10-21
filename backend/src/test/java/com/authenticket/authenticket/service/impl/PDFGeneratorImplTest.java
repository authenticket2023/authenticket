package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.service.JwtService;
import com.authenticket.authenticket.service.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PDFGeneratorImplTest {
    @Mock
    private QRCodeGenerator qrCodeGenerator;
    @Mock
    private JwtService jwtService;

    private PDFGeneratorImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new PDFGeneratorImpl(qrCodeGenerator, jwtService);
    }

    @Test
    public void testGenerateOrderDetails() throws IOException, WriterException {
        Event event = Event.builder()
                .eventId(1) // Set a unique event ID
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now()) // Set the event date
                .otherEventInfo("Additional event information")
                .eventImage("event-image.jpg") // Set the image file name
                .ticketSaleDate(LocalDateTime.now()) // Set the ticket sale date
                .reviewedBy(new Admin()) // You might need to create a dummy Admin object
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue()) // Set the review status
                .reviewRemarks("Sample review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser()) // You might need to create a dummy EventOrganiser object
                .venue(new Venue()) // You might need to create a dummy Venue object
                .artists(new HashSet<>()) // Create an empty set of artists
                .eventType(new EventType()) // You might need to create a dummy EventType object
                .ticketPricingSet(new HashSet<>()) // Create an empty set of ticket pricings
                .orderSet(new HashSet<>()) // Create an empty set of orders
                .build();

        //Create a mock ticket pricing for the ticket
        TicketPricing ticketPricing = TicketPricing.builder()
                .cat(new TicketCategory())
                .event(event)
                .price(50.0) // Set the ticket price
                .build();

        // Create a mock section for the ticket
        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(new Venue())
                .ticketCategory(new TicketCategory())
                .noOfRows(10)
                .noOfSeatsPerRow(20)
                .build();

        // Create a mock ticket for the order
        Ticket sampleTicket = Ticket.builder()
                .ticketId(1)
                .ticketPricing(ticketPricing)
                .section(section)
                .rowNo(1)
                .seatNo(2)
                .ticketHolder("Georgia")
                .order(new Order())
                .build();
        // Set other necessary properties for ticket
        Set<Ticket> ticketSet = new TreeSet<>();
        ticketSet.add(sampleTicket);

        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();

        Order order = Order.builder()
                .orderId(1) // Set a unique order ID
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .ticketSet(ticketSet).build(); // Create a sample order for testing

        InputStreamResource result = underTest.generateOrderDetails(order);
        assertNotNull(result);
    }

    @Test
    public void testGenerateOrderDetailsIllegalArgumentException() throws IOException, WriterException {
        // Arrange
        Order order = new Order(); // Create an Order object

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> underTest.generateOrderDetails(order));
    }

    @Test
    public void testGenerateTicketQRCodeIOException() throws IOException, WriterException {
        Event event = Event.builder()
                .eventId(1) // Set a unique event ID
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now()) // Set the event date
                .otherEventInfo("Additional event information")
                .eventImage("event-image.jpg") // Set the image file name
                .ticketSaleDate(LocalDateTime.now()) // Set the ticket sale date
                .reviewedBy(new Admin()) // You might need to create a dummy Admin object
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue()) // Set the review status
                .reviewRemarks("Sample review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser()) // You might need to create a dummy EventOrganiser object
                .venue(new Venue()) // You might need to create a dummy Venue object
                .artists(new HashSet<>()) // Create an empty set of artists
                .eventType(new EventType()) // You might need to create a dummy EventType object
                .ticketPricingSet(new HashSet<>()) // Create an empty set of ticket pricings
                .orderSet(new HashSet<>()) // Create an empty set of orders
                .build();

        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(true)
                .build();

        Order order = Order.builder()
                .orderId(1) // Set a unique order ID
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.PROCESSING.getStatusValue())
                .user(user)
                .event(event)
                .build();

        //Create a mock ticket pricing for the ticket
        TicketPricing ticketPricing = TicketPricing.builder()
                .cat(new TicketCategory())
                .event(event)
                .price(50.0) // Set the ticket price
                .build();

        // Create a mock section for the ticket
        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(new Venue())
                .ticketCategory(new TicketCategory())
                .noOfRows(10)
                .noOfSeatsPerRow(20)
                .build();

        // Create a mock ticket for the order
        Ticket ticket = Ticket.builder()
                .ticketId(1)
                .ticketPricing(ticketPricing)
                .section(section)
                .rowNo(1)
                .seatNo(2)
                .ticketHolder("Georgia")
                .order(order)
                .build();
        // Set other necessary properties for ticket

// Create a sample order for testing

        // Sample JWT token
        String jwtToken = "SampleJWTToken";

        // Sample QR code data
        byte[] qrCodeData = "MockedImageData".getBytes();

        // Mock the behavior of jwtService to generate a JWT token
        when(jwtService.generateTicketToken(ticket, LocalDateTime.now().plusHours(1))).thenReturn(jwtToken);

        // Mock the behavior of qrCodeGenerator to generate a QR code
        when(qrCodeGenerator.getQRCode(jwtToken, 350, 300)).thenReturn(qrCodeData);

        // Generate the ticket QR code
        assertThrows(RuntimeException.class, () -> underTest.generateTicketQRCode(ticket, LocalDateTime.now().plusHours(1)));
    }
    //Unable to unit test due to QRCode generation
}