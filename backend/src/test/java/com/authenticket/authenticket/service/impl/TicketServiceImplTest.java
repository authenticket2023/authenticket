package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private TicketPricingRepository ticketPricingRepository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    TicketDisplayDtoMapper ticketDisplayDtoMapper;

    private TicketServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketServiceImpl(
                eventRepository,
                ticketRepository,
                ticketDisplayDtoMapper,
                sectionRepository,
                ticketPricingRepository,
                orderRepository,
                venueRepository
        );
    }

    @Test
    void testFindAllTicket() {
        // Mock data
        List<Ticket> tickets = new ArrayList<>();
        // Add some tickets to the list

        when(ticketRepository.findAll()).thenReturn(tickets);

        // Test the method
        List<TicketDisplayDto> result = underTest.findAllTicket();

        // Assert the result
        assertNotNull(result);
        // Add assertions based on your specific use case
    }

    @Test
    void testFindTicketById_ExistingTicket() {

        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().organiserId(1).build()) // Create a dummy EventOrganiser
                .venue(Venue.builder().venueId(1).build()) // Create a dummy Venue
                .artists(new HashSet<>()) // Create an empty set of artists
                .eventType(new EventType()) // Create a dummy EventType
                .ticketPricingSet(new HashSet<>()) // Create an empty set of ticket pricings
                .orderSet(new HashSet<>()) // Create an empty set of orders
                .build();

        TicketPricing ticketPricing = TicketPricing.builder()
                .cat(new TicketCategory()) // Create a dummy ticket category (see below)
                .event(event) // Create a dummy event (see below)
                .price(50.0) // Set the price
                .build();

        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(new Venue()) // Set the Venue, you may need to create a helper for Venue as well
                .ticketCategory(new TicketCategory()) // Set the TicketCategory, you may need to create a helper for TicketCategory as well
                .noOfRows(10)
                .noOfSeatsPerRow(20)
                .build();

        Order order = Order.builder()
                .orderId(1)
                .orderAmount(100.0)
                .purchaseDate(LocalDate.now())
                .orderStatus(Order.Status.SUCCESS.getStatusValue())
                .user(new User())
                .event(event)
                .build();

        int ticketId = 1;
        Ticket expectedTicket = Ticket.builder()
                .ticketId(ticketId) // Set the unique identifier for the ticket
                .ticketPricing(ticketPricing) // You can set appropriate TicketPricing data here
                .section(section) // You can set appropriate Section data here
                .rowNo(5) // Set the row number
                .seatNo(10) // Set the seat number
                .ticketHolder("John Doe") // Set the name of the ticket holder
                .order(order) // You can set appropriate Order data here
                .build();

        // Mock the behavior of the ticketRepository
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(expectedTicket));

        // Act
        TicketDisplayDto result = underTest.findTicketById(ticketId);

        // Assert
        assertEquals(ticketId, result.ticketId());
    }

    @Test
    public void testFindTicketById_TicketNotFound() {
        // Set up the behavior of the repository mock
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ApiRequestException.class, () -> {
            underTest.findTicketById(1);
        });
    }

    @Test
    void testFindAllByOrderId() {
        // Mock data
        int orderId = 1;
        Order order = new Order();
        // Set properties of the order

        List<Ticket> tickets = new ArrayList<>();
        // Add some tickets to the list

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(ticketRepository.findAllByOrder(order)).thenReturn(tickets);

        // Test the method
        List<TicketDisplayDto> result = underTest.findAllByOrderId(orderId);

        // Assert the result
        assertNotNull(result);
        // Add assertions based on your specific use case
    }

    @Test
    public void testFindAllByOrderId_OrderNotFound() {
        // Mock the behavior of orderRepository.findById() to return an empty Optional
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Ensure that an ApiRequestException is thrown when the order is not found
        assertThrows(ApiRequestException.class, () -> underTest.findAllByOrderId(1));
    }

    @Test
    void testSaveTicket() {
        // Mock data
        Ticket ticket = new Ticket();
        // Set properties of the ticket

        when(ticketRepository.save(ticket)).thenReturn(ticket);

        // Test the method
        Ticket result = underTest.saveTicket(ticket);

        // Assert the result
        assertNotNull(result);
        // Add assertions based on your specific use case
    }

    @Test
    public void testAllocateSeatsWithIllegalArgument() {
        // Create test data for Event, Venue, Section

        Venue venue = Venue.builder()
                .venueId(1)
                .venueName("Sample Venue")
                .venueLocation("Sample Location")
                .venueDescription("This is a sample venue for testing purposes.")
                .venueImage("sample_image.jpg")
                .sections(new ArrayList<>()) // Empty sections for this example
                .build();

        Event event = Event.builder()
                .eventId(1)
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().organiserId(1).build()) // Create a dummy EventOrganiser
                .venue(venue) // Create a dummy Venue
                .artists(new HashSet<>()) // Create an empty set of artists
                .eventType(new EventType()) // Create a dummy EventType
                .ticketPricingSet(new HashSet<>()) // Create an empty set of ticket pricings
                .orderSet(new HashSet<>()) // Create an empty set of orders
                .build();

        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(venue) // Set the Venue, you may need to create a helper for Venue as well
                .ticketCategory(new TicketCategory()) // Set the TicketCategory, you may need to create a helper for TicketCategory as well
                .noOfRows(10)
                .noOfSeatsPerRow(20)
                .build();

        // Mock the behavior of eventRepository and sectionRepository
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(sectionRepository.findById(any())).thenReturn(Optional.of(section));

        // Call the method
        try {
            underTest.allocateSeats(event.getEventId(), section.getSectionId(), 5);
        } catch (IllegalArgumentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Section not connected to event venue", e.getMessage());
        }
    }

    @Test
    public void testAllocateSeatsWithNonExistentEvent() {
        // Mock the behavior of eventRepository to return null (non-existent event)
        when(eventRepository.findById(any())).thenReturn(Optional.empty());

        // Call the method and expect a NonExistentException
        try {
            underTest.allocateSeats(1, "sectionId", 5);
        } catch (NonExistentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Event does not exist", e.getMessage());
        }

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(any());
        // Ensure sectionRepository and ticketRepository are not called
        verify(sectionRepository, never()).findById(any());
        verify(ticketRepository, never()).deleteAll(any());
    }

    @Test
    public void testAllocateSeatsWithNonExistentSection() {
        // Create test data for Event
        Event event = new Event();
        event.setEventId(1);

        // Mock the behavior of eventRepository to return the event
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        // Mock the behavior of sectionRepository to return null (non-existent section)
        when(sectionRepository.findById(any())).thenReturn(Optional.empty());

        // Call the method and expect a NonExistentException
        try {
            underTest.allocateSeats(event.getEventId(), "sectionId", 5);
        } catch (NonExistentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Section does not exist", e.getMessage());
        }

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(event.getEventId());
        verify(sectionRepository, times(1)).findById(any());
        // Ensure ticketRepository is not called
        verify(ticketRepository, never()).deleteAll(any());
    }

    @Test
    public void testAllocateSeatsWithInvalidTicketsToPurchase() {
        // Create a test data for Event and Section
        Event event = new Event();
        event.setEventId(1);

        Section section = new Section();
        section.setNoOfRows(5);
        section.setNoOfSeatsPerRow(10);

        // Mock the behavior of eventRepository and sectionRepository
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(sectionRepository.findById(any())).thenReturn(Optional.of(section));

        // Call the method with an invalid number of tickets to purchase
        try {
            underTest.allocateSeats(event.getEventId(), section.getSectionId(), 6);  // 6 tickets, which is invalid
        } catch (IllegalArgumentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Tickets To Purchase Must Be Between 1 to 5", e.getMessage());
        }

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(event.getEventId());
        verify(sectionRepository, times(1)).findById(any());
        // Ensure ticketRepository is not called
        verify(ticketRepository, never()).deleteAll(any());
    }

    @Test
    public void testAllocateSeatsWithInsufficientSeats() {
        // Create test data for Event and Section
        Venue venue = Venue.builder()
                .venueId(1)
                .venueName("Sample Venue")
                .venueLocation("Sample Location")
                .venueDescription("This is a sample venue for testing purposes.")
                .venueImage("sample_image.jpg")
                .sections(null) // Empty sections for this example
                .build();

        Event event = Event.builder()
                .eventId(1)
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().organiserId(1).build()) // Create a dummy EventOrganiser
                .venue(venue) // Create a dummy Venue
                .artists(new HashSet<>()) // Create an empty set of artists
                .eventType(new EventType()) // Create a dummy EventType
                .ticketPricingSet(new HashSet<>()) // Create an empty set of ticket pricings
                .orderSet(new HashSet<>()) // Create an empty set of orders
                .build();

        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(venue) // Set the Venue, you may need to create a helper for Venue as well
                .ticketCategory(new TicketCategory()) // Set the TicketCategory, you may need to create a helper for TicketCategory as well
                .noOfRows(1)
                .noOfSeatsPerRow(1)
                .build();
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(section);
        venue.setSections(sectionList);

        // Mock the behavior of eventRepository and sectionRepository
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(sectionRepository.findById(any())).thenReturn(Optional.of(section));

        // Call the method with a request for more tickets than available seats
        try {
            underTest.allocateSeats(event.getEventId(), section.getSectionId(), 2);
        } catch (IllegalArgumentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Insufficient seats for event", e.getMessage());
        }

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(event.getEventId());
        verify(sectionRepository, times(1)).findById(any());
        // Ensure ticketRepository is not called
        verify(ticketRepository, never()).deleteAll(any());
    }

    @Test
    void testGetSeatCombinationRank() {
        // Test for ticketCount of 5
        int ticketCount = 5;

        String[] result = underTest.getSeatCombinationRank(ticketCount);

        assertNotNull(result);
        assertEquals(7, result.length);

        // Test for ticketCount of 2
        ticketCount = 2;

        result = underTest.getSeatCombinationRank(ticketCount);

        assertNotNull(result);
        assertEquals(2, result.length);

        // Test for ticketCount of 1
        ticketCount = 1;

        result = underTest.getSeatCombinationRank(ticketCount);

        assertNotNull(result);
        assertEquals(1, result.length);

        // Test for an invalid ticketCount
        ticketCount = -1;

        result = underTest.getSeatCombinationRank(ticketCount);

        assertNull(result);
    }

    @Test
    void testGetEventHasTickets() {
        // Mock data
        Event event = Event.builder()
                .eventName("Sample Event")
                .eventDescription("This is a sample event description.")
                .eventDate(LocalDateTime.now().plusDays(7))
                .otherEventInfo("Additional event information goes here.")
                .eventImage("event.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(1))
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Event is approved")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(EventOrganiser.builder().organiserId(1).build()) // Create a dummy EventOrganiser
                .venue(Venue.builder().venueId(1).build()) // Create a dummy Venue
                .artists(new HashSet<>()) // Create an empty set of artists
                .eventType(new EventType()) // Create a dummy EventType
                .ticketPricingSet(new HashSet<>()) // Create an empty set of ticket pricings
                .orderSet(new HashSet<>()) // Create an empty set of orders
                .build();
        // Set properties of the event

        Venue venue = Venue.builder()
                .venueId(1)  // Set the unique identifier for the venue
                .venueName("Sample Venue")  // Set the name of the venue
                .venueLocation("Sample Location")  // Set the location of the venue
                .venueDescription("This is a sample venue description.")  // Set the venue description
                .venueImage("venue.jpg")  // Set the image associated with the venue
                .sections(new ArrayList<>())  // Create an empty list of sections
                .build();

        // Set properties of the venue

        when(venueRepository.findNoOfSeatsByVenue(venue.getVenueId())).thenReturn(100); // Adjust the number as needed
        when(ticketRepository.countTicketsByOrder_Event(event)).thenReturn(80); // Adjust the number as needed

        boolean result = underTest.getEventHasTickets(event);

        assertTrue(result);

        // Test with sold out event
        when(venueRepository.findNoOfSeatsByVenue(venue.getVenueId())).thenReturn(100); // Adjust the number as needed
        when(ticketRepository.countTicketsByOrder_Event(event)).thenReturn(100); // Adjust the number as needed

        result = underTest.getEventHasTickets(event);

        assertFalse(result);
    }

    @Test
    public void testRemoveAllTickets_ValidTicketIds() {
        // Mock the behavior of ticketRepository to return valid tickets when given valid ticket IDs.
        List<Integer> validTicketIds = Arrays.asList(1, 2, 3); // Assuming these IDs are valid.
        List<Ticket> validTickets = new ArrayList<>();
        for (Integer id : validTicketIds) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(id);
            validTickets.add(ticket);
        }
        when(ticketRepository.findAllById(validTicketIds)).thenReturn(validTickets);

        // Call the method and verify that tickets are deleted.
        underTest.removeAllTickets(validTicketIds);

        // Assert that ticketRepository.deleteAllInBatch() was called with validTickets.
        verify(ticketRepository).deleteAllInBatch(validTickets);
    }

    @Test
    public void testRemoveAllTickets_MissingTicketIds() {
        // Mock the behavior of ticketRepository to return valid tickets when given valid ticket IDs.
        List<Integer> validTicketIds = Arrays.asList(1, 2, 3); // Assuming these IDs are valid.
        List<Integer> invalidTicketIds = Arrays.asList(1, 2);
        List<Ticket> invalidTickets = new ArrayList<>();
        for (Integer id : invalidTicketIds) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(id);
            invalidTickets.add(ticket);
        }
        when(ticketRepository.findAllById(validTicketIds)).thenReturn(invalidTickets);

        // Call the method and expect an exception to be thrown.
        try {
            underTest.removeAllTickets(validTicketIds);
        } catch (IllegalArgumentException e) {
            // Verify that the exception message contains the missing ticket IDs.
            String errorMessage = e.getMessage();
            assert(errorMessage.contains("Error deleting tickets"));
        }

        // Assert that ticketRepository.deleteAllInBatch() was not called.
        verify(ticketRepository, never()).deleteAllInBatch();
    }


    @Test
    public void testRemoveAllTickets_NoTicketIdsProvided() {
        // Call the method with an empty list of ticket IDs.
        List<Integer> emptyList = new ArrayList<>();
        List<Ticket> emptyTicketList = new ArrayList<>();
        underTest.removeAllTickets(emptyList);

        // Verify that ticketRepository.deleteAllInBatch() was called with an empty list.
        verify(ticketRepository).deleteAllInBatch(emptyTicketList);
    }

    @Test
    public void testGetMaxConsecutiveSeatsForSection() {
        // Create test data for Event, Venue, and Section
        Event event = new Event();
        event.setEventId(1);  // Set the event ID

        Venue venue = new Venue();
        // Set venue properties

        Section section = new Section();
        // Set section properties
        section.setNoOfRows(5);
        section.setNoOfSeatsPerRow(10);

        // Mock the behavior of eventRepository and sectionRepository
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(sectionRepository.findById(any())).thenReturn(Optional.of(section));

        // Mock the behavior of ticketRepository
        when(ticketRepository.countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(any(), any(), any()))
                .thenReturn(0);  // You can adjust the return value as needed

        // Call the method
        Integer maxConsecutiveSeats = underTest.getMaxConsecutiveSeatsForSection(event.getEventId(), section.getSectionId());

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(event.getEventId());
        verify(sectionRepository, times(1)).findById(any());
        verify(ticketRepository, atLeastOnce()).countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(any(), any(), any());

        // Add assertions to check the result
        // You can assert that maxConsecutiveSeats matches your expectations
        // In this example, we expect it to be 10, as there are 10 consecutive seats available in a row.

        assertEquals(Integer.valueOf(10), maxConsecutiveSeats);
    }

    @Test
    public void testGetMaxConsecutiveSeatsForSectionWithNonExistentEvent() {
        // Mock the behavior of eventRepository to return null (non-existent event)
        when(eventRepository.findById(any())).thenReturn(Optional.empty());

        // Call the method and expect a NonExistentException
        try {
            underTest.getMaxConsecutiveSeatsForSection(1, "sectionId");
        } catch (NonExistentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Event does not exist", e.getMessage());
        }

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(any());
        // Ensure sectionRepository and ticketRepository are not called
        verify(sectionRepository, never()).findById(any());
        verify(ticketRepository, never()).countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(any(), any(), any());
    }

    @Test
    public void testGetMaxConsecutiveSeatsForSectionWithNonExistentSection() {
        // Create test data for Event
        Event event = new Event();
        event.setEventId(1);

        // Mock the behavior of eventRepository to return the event
        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        // Mock the behavior of sectionRepository to return null (non-existent section)
        when(sectionRepository.findById(any())).thenReturn(Optional.empty());

        // Call the method and expect a NonExistentException
        try {
            underTest.getMaxConsecutiveSeatsForSection(event.getEventId(), "sectionId");
        } catch (NonExistentException e) {
            // You can customize the exception message and further assert it if needed
            assertEquals("Section does not exist", e.getMessage());
        }

        // Verify that the necessary repository methods were called
        verify(eventRepository, times(1)).findById(event.getEventId());
        verify(sectionRepository, times(1)).findById(any());
        // Ensure ticketRepository is not called
        verify(ticketRepository, never()).countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(any(), any(), any());
    }

    @Test
    void testGetNumberOfTicketsPurchaseable() {
        // Mock an Event object
        Event mockEvent = new Event();
        mockEvent.setEventId(1);

        // Mock a User object
        User mockUser = new User();
        mockUser.setUserId(1);

        // Mock the behavior of the ticketRepository.countAllByOrder_EventAndOrder_User method
        when(ticketRepository.countAllByOrder_EventAndOrder_User(mockEvent, mockUser)).thenReturn(2);

        // Set the maximum allowed tickets per user
        int maxTicketsPerUser = 5;

        // Call the method you want to test
        int numberOfTicketsPurchaseable = underTest.getNumberOfTicketsPurchaseable(mockEvent, mockUser);

        // Verify that the countAllByOrder_EventAndOrder_User method was called with the correct arguments
        verify(ticketRepository, times(1)).countAllByOrder_EventAndOrder_User(mockEvent, mockUser);

        // Verify that the result is correct
        assertEquals(maxTicketsPerUser - 2, numberOfTicketsPurchaseable);
    }

    @Test
    void testSetCheckIn() {
        // Mock a Ticket object
        Ticket mockTicket = new Ticket();
        mockTicket.setTicketId(1);

        // Mock the behavior of the ticketRepository.findById method
        when(ticketRepository.findById(1)).thenReturn(Optional.of(mockTicket));

        // Call the method you want to test
        underTest.setCheckIn(1, true);

        // Verify that the save method of the repository was called
        verify(ticketRepository, times(1)).save(mockTicket);

        // Verify that the checkedIn property was set to true
        assertTrue(mockTicket.getCheckedIn());
    }

    @Test
    void testSetCheckInNonExistent() {
        // Mock the behavior of the ticketRepository.findById method
        when(ticketRepository.findById(1)).thenReturn(Optional.empty());

        // Call the method you want to test and expect an exception
        assertThrows(NonExistentException.class, () -> underTest.setCheckIn(1, true));

        // Verify that the save method of the repository was not called
        verify(ticketRepository, never()).save(any());
    }
}