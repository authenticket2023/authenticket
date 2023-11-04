package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.QueueRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceImplTest {

    private QueueServiceImpl underTest;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private TicketRepository ticketRepository;
    @BeforeEach
    void setUp(){
        underTest = new QueueServiceImpl(
                queueRepository,
                venueRepository,
                ticketRepository
        );
    }

    @Test
    public void testGetPosition_UserInQueue_ReturnsPosition() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();
        Queue queue = new Queue(user, event, false, LocalDateTime.now());

        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.of(queue));
        int position = underTest.getPosition(user, event);
        assertEquals(1, position);  // The position should be 1
    }

    @Test
    public void testGetPosition_UserNotInQueue_ReturnsMinusOne() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.empty());
        int position = underTest.getPosition(user, event);
        assertEquals(-1, position);  // User is not in the queue, so the position should be -1
    }

    @Test
    public void testTotalInQueue_ReturnsTotalUsersInQueue() {

        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();
        when(queueRepository.countAllByEventAndCanPurchase(event, false)).thenReturn(5);

        int totalInQueue = underTest.getTotalInQueue(event);
        assertEquals(5, totalInQueue);
    }

    @Test
    public void testCanPurchase_UserInQueue_ReturnsTrue() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();
        Queue queue = new Queue(user, event, true, LocalDateTime.now());

        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.of(queue));
        boolean canPurchase = underTest.canPurchase(user, event);
        assertTrue(canPurchase);
    }

    @Test
    public void testCanPurchase_UserNotInQueue_ThrowsNonExistentException() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.empty());
        assertThrows(NonExistentException.class, () -> underTest.canPurchase(user, event));
    }

    @Test
    public void testAddToQueue_UserNotInQueue_AddsUserToQueue() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        when(queueRepository.existsById(any(EventUserId.class))).thenReturn(false);
        underTest.addToQueue(user, event);

        verify(queueRepository).save(any(Queue.class));
    }

    @Test
    public void testAddToQueue_UserAlreadyInQueue_ThrowsAlreadyExistsException() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        when(queueRepository.existsById(any(EventUserId.class))).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> underTest.addToQueue(user, event));
    }

    @Test
    public void testRemoveFromQueue_UserInQueue_RemovesUserFromQueue() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();
        Queue queue = new Queue(user, event, true, LocalDateTime.now());

        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.of(queue));
        underTest.removeFromQueue(user, event);

        verify(queueRepository).delete(any(Queue.class));
    }

    @Test
    public void testRemoveFromQueue_UserNotInQueue_ThrowsNonExistentException() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.empty());
        assertThrows(NonExistentException.class, () -> underTest.removeFromQueue(user, event));
    }

    @Test
    public void testGetPosition_UserInQueue() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        Queue queue = new Queue(user, event, false, LocalDateTime.now());
        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.of(queue));
        int position = underTest.getPosition(user, event);
        assertEquals(1, position);
    }

    @Test
    public void testGetPosition_UserCanPurchase() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        Queue queue = new Queue(user, event, true, LocalDateTime.now());
        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.of(queue));
        int position = underTest.getPosition(user, event);
        assertEquals(0, position);
    }

    @Test
    public void testGetPosition_UserNotInQueue() {
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(new Venue())
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();
        when(queueRepository.findById(any(EventUserId.class))).thenReturn(Optional.empty());
        int position = underTest.getPosition(user, event);
        assertEquals(-1, position);
    }

    @Test
    public void testUpdatePurchasingUsersInQueue_TimeAfterSaleDate() {
        // Create a mock Event and an EventUserId
        Venue venue = Venue.builder()
                .venueId(99)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();
        Event event = Event.builder()
                .eventId(2)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().minusDays(1)) // Sale date is in the past
                .reviewedBy(new Admin())
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(new EventOrganiser())
                .venue(venue)
                .eventType(new EventType())
                .artists(new HashSet<>())
                .build();

        // Mock the queueRepository
        when(queueRepository.countAllByEventAndCanPurchase(event, true)).thenReturn(3);

        // Call the method to be tested
        underTest.updatePurchasingUsersInQueue(event);

        // Verify that queueRepository.findFirstByEventAndCanPurchaseFalseOrderByTimeAsc was not invoked
        verify(queueRepository, never()).findFirstByEventAndCanPurchaseFalseOrderByTimeAsc(eq(event));

        // Verify that queueRepository.countAllByEventAndCanPurchase was invoked once with the specific event and true
        verify(queueRepository, times(1)).countAllByEventAndCanPurchase(eq(event), eq(true));
    }
}