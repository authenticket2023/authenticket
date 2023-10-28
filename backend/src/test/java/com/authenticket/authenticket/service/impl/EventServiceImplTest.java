package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDtoMapper;
import com.authenticket.authenticket.dto.section.SectionDtoMapper;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private FeaturedEventRepository featuredEventRepository;

    @Mock
    private TicketCategoryRepository ticketCategoryRepository;

    @Mock
    private TicketPricingRepository ticketPricingRepository;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @Mock
    private EmailService emailService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private OrderRepository orderRepository;

    private EventServiceImpl underTest;

    @BeforeEach
    void setUp() {
        TicketDisplayDtoMapper ticketDisplayDtoMapper = new TicketDisplayDtoMapper();
        TicketServiceImpl ticketService = new TicketServiceImpl(
                eventRepository,
                ticketRepository,
                ticketDisplayDtoMapper,
                sectionRepository,
                ticketPricingRepository,
                orderRepository,
                venueRepository
        );

        AdminDtoMapper adminDtoMapper = new AdminDtoMapper(passwordEncoder);
        EventOrganiserDtoMapper eventOrganiserDtoMapper = new EventOrganiserDtoMapper(passwordEncoder, adminDtoMapper);
        EventTicketCategoryDtoMapper eventTicketCategoryDtoMapper = new EventTicketCategoryDtoMapper();
        ArtistDtoMapper artistDtoMapper = new ArtistDtoMapper();
        EventDtoMapper eventDtoMapper = new EventDtoMapper(
                eventOrganiserDtoMapper,
                eventTicketCategoryDtoMapper,
                artistDtoMapper,
                adminDtoMapper,
                eventRepository,
                venueRepository,
                ticketRepository);

        SectionDtoMapper sectionDtoMapper = new SectionDtoMapper(ticketService);
        emailService = new EmailServiceImpl(javaMailSender);

        underTest = new EventServiceImpl(eventRepository,
                artistRepository,
                featuredEventRepository,
                ticketCategoryRepository,
                ticketPricingRepository,
                eventDtoMapper,
                artistDtoMapper,
                emailService,
                ticketRepository,
                sectionDtoMapper
        );
    }

    @Test
    public void testFindAllPublicEvent() {
        // Prepare test data
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        Pageable pageable = PageRequest.of(1, 1)/* create Pageable object for testing */;
        Page<Event> mockPage = new PageImpl<>(eventList, pageable, 1);

        when(eventRepository.findAllByReviewStatusAndDeletedAtIsNull(any(), any())).thenReturn(mockPage);
//        when(eventDtoMapper.mapEventHomeDto(eventList)).thenReturn(expectedEventList);

        // Call the method to be tested
        List<EventHomeDto> result = underTest.findAllPublicEvent(pageable);

        // Assert and verify
        assertNotNull(result);
        verify(eventRepository, times(1)).findAllByReviewStatusAndDeletedAtIsNull(any(), any());
    }

    @Test
    public void testFindEventById() {
        // Prepare test data
        int eventId = 1;
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(artists)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Call the method to be tested
        OverallEventDto result = underTest.findEventById(eventId);

        // Assert and verify
        assertNotNull(result);
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void testFindEventById_NonExistent() {
        // Prepare test data
        int eventId = 1;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Call the method to be tested
        OverallEventDto result = underTest.findEventById(eventId);

        // Assert and verify
        assertNull(result);
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void testFindAllEvent() {
        // Prepare test data
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        when(eventRepository.findAllByOrderByEventIdAsc()).thenReturn(eventList);

        // Call the method to be tested
        List<EventAdminDisplayDto> result = underTest.findAllEvent();

        // Assert and verify
        assertNotNull(result);
        verify(eventRepository, times(1)).findAllByOrderByEventIdAsc();
    }

    @Test
    void testFindRecentlyAddedEvents() {
        // Create a pageable object for testing
        Pageable pageable = PageRequest.of(0, 10);

        // Create a list of Event entities that represent the expected result
        List<Event> expectedEvents = new ArrayList<>();

        // Mock the behavior of eventRepository to return a Page of events
        when(eventRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                Event.ReviewStatus.APPROVED.getStatusValue(), pageable))
                .thenReturn(new PageImpl<>(expectedEvents));

        // Mock the behavior of eventDtoMapper to map the events
        List<EventHomeDto> expectedEventHomeDtos = new ArrayList<>();
        // Add the expected EventHomeDtos to the list

        // Call the method to be tested
        List<EventHomeDto> result = underTest.findRecentlyAddedEvents(pageable);

        // Verify that the repository method was called with the correct parameters
        verify(eventRepository).findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                Event.ReviewStatus.APPROVED.getStatusValue(), pageable);

        // Assert the result
        assertEquals(expectedEventHomeDtos, result);
    }

    @Test
    void testFindFeaturedEvents() {
        // Create a pageable object for testing
        Pageable pageable = PageRequest.of(0, 10);

        // Create a list of FeaturedEvent entities that represent the expected result
        List<FeaturedEvent> expectedFeaturedEvents = new ArrayList<>();

        // Mock the behavior of featuredEventRepository to return a Page of featured events
        when(featuredEventRepository.findAllFeaturedEventsByStartDateBeforeAndEndDateAfter(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))  // Use eq() for pageable
                .thenReturn(new PageImpl<>(expectedFeaturedEvents));

        // Call the method to be tested
        List<FeaturedEventDto> result = underTest.findFeaturedEvents(pageable);

        // Verify that the repository method was called with the correct parameters
        verify(featuredEventRepository).findAllFeaturedEventsByStartDateBeforeAndEndDateAfter(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));

        // Assert the result
        assertNotNull(result);
    }

    @Test
    void testFindBestSellerEvents() {
        // Create a list of events that represent the expected result
        List<Object[]> expectedEvents = new ArrayList<>();

        // Mock the behavior of eventRepository to return a list of events
        when(eventRepository.findBestSellerEvents()).thenReturn(expectedEvents);

        // Call the method to be tested
        List<EventHomeDto> result = underTest.findBestSellerEvents();

        // Verify that the repository method was called
        verify(eventRepository).findBestSellerEvents();

        // Assert the result
        assertNotNull(result);
        // You can add more specific assertions based on your test data and expectations
    }

    @Test
    void testFindUpcomingEventsByTicketSalesDate() {
        // Create a pageable object for testing
        Pageable pageable = PageRequest.of(0, 10);

        // Create a list of events that represent the expected result
        List<Event> expectedEvents = new ArrayList<>();

        // Mock the behavior of eventRepository to return a Page of events
        when(eventRepository.findAllByReviewStatusAndTicketSaleDateAfterAndDeletedAtIsNullOrderByTicketSaleDateAsc(
                any(), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(new PageImpl<>(expectedEvents));

        // Call the method to be tested
        List<EventHomeDto> result = underTest.findUpcomingEventsByTicketSalesDate(pageable);

        // Verify that the repository method was called with the correct parameters
        verify(eventRepository).findAllByReviewStatusAndTicketSaleDateAfterAndDeletedAtIsNullOrderByTicketSaleDateAsc(
                any(), any(LocalDateTime.class), eq(pageable));

        // Assert the result
        assertNotNull(result);
        // You can add more specific assertions based on your test data and expectations
    }

    @Test
    public void testFindCurrentEventsByEventDate() {
        LocalDateTime currentDate = LocalDateTime.now();

        // Create a Pageable instance for testing (you can customize it)
        Pageable pageable = Pageable.unpaged();

        // Create a list of Event entities for the repository to return
        List<Event> events = new ArrayList<>();
        // Add some sample events to the list

        // Create a Page object from the list of events
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        // Mock the behavior of eventRepository
        when(eventRepository.findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullOrderByEventDateAsc(
                any(), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(eventPage);
        // Call the method you want to test
        List<EventHomeDto> result = underTest.findCurrentEventsByEventDate(pageable);

        // Verify that the repository and mapper were called with the correct arguments
        verify(eventRepository).findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullOrderByEventDateAsc(
                any(), any(LocalDateTime.class), eq(pageable));

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testFindPastEventsByEventDate() {
        LocalDateTime currentDate = LocalDateTime.now();

        // Create a Pageable instance for testing (you can customize it)
        Pageable pageable = Pageable.unpaged();

        // Create a list of Event entities for the repository to return
        List<Event> events = new ArrayList<>();
        // Add some sample events to the list

        // Create a Page object from the list of events
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        // Mock the behavior of eventRepository
        when(eventRepository.findAllByReviewStatusAndEventDateBeforeAndDeletedAtIsNullOrderByEventDateDesc(
                any(), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(eventPage);

        // Call the method you want to test
        List<EventHomeDto> result = underTest.findPastEventsByEventDate(pageable);

        // Verify that the repository and mapper were called with the correct arguments
        verify(eventRepository).findAllByReviewStatusAndEventDateBeforeAndDeletedAtIsNullOrderByEventDateDesc(
                any(), any(LocalDateTime.class), eq(pageable));

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testFindEventsByReviewStatus() {
        String reviewStatus = "APPROVED"; // Change this to the appropriate status value

        // Create a list of Event entities for the repository to return
        List<Event> events = new ArrayList<>();
        // Add some sample events to the list

        // Mock the behavior of eventRepository
        when(eventRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(reviewStatus))
                .thenReturn(events);

        // Call the method you want to test
        List<EventDisplayDto> result = underTest.findEventsByReviewStatus(reviewStatus);

        // Verify that the repository and mapper were called with the correct arguments
        verify(eventRepository).findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(reviewStatus);

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testFindEventsByVenue() {
        int venueId = 1; // Change this to the appropriate venue ID
        Pageable pageable = Pageable.unpaged(); // You can customize Pageable as needed

        // Create a list of Event entities for the repository to return
        List<Event> events = new ArrayList<>();
        // Add some sample events to the list

        // Mock the behavior of eventRepository
        when(eventRepository.findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullOrderByEventDateDesc(
                Event.ReviewStatus.APPROVED.getStatusValue(), venueId, pageable))
                .thenReturn(new PageImpl<>(events));

        // Call the method you want to test
        List<EventHomeDto> result = underTest.findEventsByVenue(venueId, pageable);

        // Verify that the repository and mapper were called with the correct arguments
        verify(eventRepository).findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullOrderByEventDateDesc(
                Event.ReviewStatus.APPROVED.getStatusValue(), venueId, pageable);

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testFindPastEventsByVenue() {
        int venueId = 1; // Change this to the appropriate venue ID
        Pageable pageable = Pageable.unpaged(); // You can customize Pageable as needed

        // Create a list of Event entities for the repository to return
        List<Event> events = new ArrayList<>();
        // Add some sample events to the list

        // Mock the behavior of eventRepository
        when(eventRepository
                .findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullAndEventDateBeforeOrderByEventDateDesc(
                        any(), anyInt(), eq(pageable), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(events));

        // Call the method you want to test
        List<EventHomeDto> result = underTest.findPastEventsByVenue(venueId, pageable);

        // Verify that the repository and mapper were called with the correct arguments
        verify(eventRepository).findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullAndEventDateBeforeOrderByEventDateDesc(
                any(), anyInt(), eq(pageable), any(LocalDateTime.class));

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testFindUpcomingEventsByVenue() {
        int venueId = 1; // Change this to the appropriate venue ID
        Pageable pageable = Pageable.unpaged(); // You can customize Pageable as needed
        LocalDateTime currentDate = LocalDateTime.now(); // The current date for comparison

        // Create a list of Event entities for the repository to return
        List<Event> events = new ArrayList<>();
        // Add some sample events to the list

        // Mock the behavior of eventRepository
        when(eventRepository
                .findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullAndEventDateAfterOrderByEventDateDesc(
                        any(), anyInt(), eq(pageable), any(LocalDateTime.class)))
                .thenReturn(new PageImpl<>(events));

        // Call the method you want to test
        List<EventHomeDto> result = underTest.findUpcomingEventsByVenue(venueId, pageable);

        // Verify that the repository and mapper were called with the correct arguments
        verify(eventRepository).findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullAndEventDateAfterOrderByEventDateDesc(
                any(), anyInt(), eq(pageable), any(LocalDateTime.class));

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testSaveEvent() {
        // Create a sample Event object to be saved
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();

        // Mock the behavior of eventRepository.save
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Call the method you want to test
        Event result = underTest.saveEvent(event);

        // Verify that eventRepository.save was called with the expected argument
        verify(eventRepository).save(event);

        // Verify that the result matches your expectations
        assertEquals(event, result);
    }

    @Test
    public void testSaveFeaturedEvent() {
        // Create a sample Event object to be saved
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();

        // Create a sample FeaturedEvent object to be saved
        FeaturedEvent dummyFeaturedEvent = FeaturedEvent.builder()
                .featuredId(1)
                .event(event)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .admin(new Admin())
                .build();

        // Mock the behavior of featuredEventRepository.save
        when(featuredEventRepository.save(any(FeaturedEvent.class))).thenReturn(dummyFeaturedEvent);

        // Call the method you want to test
        FeaturedEventDto result = underTest.saveFeaturedEvent(dummyFeaturedEvent);

        // Verify that featuredEventRepository.save was called with the expected argument
        verify(featuredEventRepository).save(dummyFeaturedEvent);

        // Verify that the result matches your expectations
        assertNotNull(result);
    }

    @Test
    public void testUpdateEvent_Success() {
        // Arrange
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();

        EventUpdateDto dummyEventUpdateDto = new EventUpdateDto(
                event.getEventId(),             // Integer
                event.getEventName(), // String
                event.getEventDescription(), // String
                event.getEventDate(),  // LocalDateTime
                "Event Location",    // String
                event.getOtherEventInfo(),  // String
                LocalDateTime.now(),  // LocalDateTime
                new Venue(),         // Venue (create a dummy Venue object)
                new EventType(),     // EventType (create a dummy EventType object)
                "Review Remarks",    // String
                "APPROVED",          // String (assuming this is a valid review status)
                new Admin()          // Admin (create a dummy Admin object)
        );

        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));

        // Mock any other dependencies as needed

        // Act
        Event updatedEvent = underTest.updateEvent(dummyEventUpdateDto);

        // Assert
        assertEquals(dummyEventUpdateDto.eventId(), updatedEvent.getEventId());
        // Add more assertions based on the expected behavior
    }

    @Test
    public void testUpdateEvent_NonExistentEvent() {
        // Arrange
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();

        EventUpdateDto dummyEventUpdateDto = new EventUpdateDto(
                event.getEventId(),             // Integer
                event.getEventName(), // String
                event.getEventDescription(), // String
                event.getEventDate(),  // LocalDateTime
                "Event Location",    // String
                event.getOtherEventInfo(),  // String
                LocalDateTime.now(),  // LocalDateTime
                new Venue(),         // Venue (create a dummy Venue object)
                new EventType(),     // EventType (create a dummy EventType object)
                "Review Remarks",    // String
                "APPROVED",          // String (assuming this is a valid review status)
                new Admin()          // Admin (create a dummy Admin object)
        );
        when(eventRepository.findById(dummyEventUpdateDto.eventId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.updateEvent(dummyEventUpdateDto);
        });
    }

    @Test
    public void testDeleteEventSuccess() {
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();

        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        String result = underTest.deleteEvent(event.getEventId());

        assertEquals(String.format("Event %d is successfully deleted.", event.getEventId()), result);
    }

    @Test
    public void testDeleteEventAlreadyDeleted() {
        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(99)
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
                .artists(artists)
                .build();
        event.setDeletedAt(LocalDateTime.now());

        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.of(event));

        String result = underTest.deleteEvent(event.getEventId());

        assertEquals(String.format("Event %d is already deleted.", event.getEventId()), result);
    }

    @Test
    public void testDeleteEventNonExistentEvent() {
        Integer eventId = 1;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NonExistentException.class, () -> underTest.deleteEvent(eventId));
    }

    @Test
    public void testAddArtistToEventSuccess() {
        int eventId = 99;

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(null)
                .build();

        int artistId = 1;
        Artist artist = new Artist();
        artist.setArtistId(artistId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        when(eventRepository.save(event)).thenReturn(event);

        EventDisplayDto result = underTest.addArtistToEvent(artistId, eventId);

        assertNotNull(result);
        assertEquals(1, result.artistSet().size());
        assertTrue(result.artistSet().contains(artist));
    }

    @Test
    public void testAddArtistToEventArtistAlreadyLinked() {
        int eventId = 99;
        Set<Artist> artists = new HashSet<>();

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(null)
                .build();

        int artistId = 1;
        Artist artist = new Artist();
        artist.setArtistId(artistId);
        artists.add(artist);
        event.setArtists(artists);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        assertThrows(AlreadyExistsException.class, () -> underTest.addArtistToEvent(artistId, eventId));
    }

    @Test
    public void testAddArtistToEventNonExistentArtist() {
        Integer eventId = 1;
        Integer artistId = 2;

        Event event = new Event();
        event.setEventId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(NonExistentException.class, () -> underTest.addArtistToEvent(artistId, eventId));
    }

    @Test
    public void testAddArtistToEventNonExistentEvent() {
        Integer eventId = 1;
        Integer artistId = 2;

        Artist artist = new Artist();
        artist.setArtistId(artistId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        assertThrows(NonExistentException.class, () -> underTest.addArtistToEvent(artistId, eventId));
    }

    @Test
    public void testRemoveAllArtistFromEvent() {
        Integer eventId = 1;

        underTest.removeAllArtistFromEvent(eventId);

        // Verify that the deleteAllArtistByEventId method was called with the expected eventId
        verify(eventRepository).deleteAllArtistByEventId(eventId);
    }

    @Test
    public void testAddTicketCategory() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        Event event = new Event(); // Create a dummy Event object
        TicketCategory category = new TicketCategory(); // Create a dummy TicketCategory object

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));
        when(ticketPricingRepository.findById(any(EventTicketCategoryId.class))).thenReturn(Optional.empty());

        // Act
        EventDisplayDto result = underTest.addTicketCategory(catId, eventId, price);

        // Assert
        assertNotNull(result);
        verify(eventRepository, times(1)).save(event);
        verify(ticketPricingRepository, times(1)).findById(any(EventTicketCategoryId.class));
    }

    @Test
    public void testAddTicketCategoryWhenAlreadyLinked() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        Event event = new Event(); // Create a dummy Event object
        TicketCategory category = new TicketCategory(); // Create a dummy TicketCategory object

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));
        when(ticketPricingRepository.findById(any(EventTicketCategoryId.class))).thenReturn(Optional.of(new TicketPricing()));

        // Act and Assert
        assertThrows(AlreadyExistsException.class, () -> {
            underTest.addTicketCategory(catId, eventId, price);
        });

        verify(eventRepository, never()).save(event);
    }

    @Test
    public void testAddTicketCategoryWhenCategoryNotExists() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        Event event = new Event(); // Create a dummy Event object

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.addTicketCategory(catId, eventId, price);
        });

        verify(eventRepository, never()).save(event);
    }

    @Test
    public void testAddTicketCategoryWhenEventNotExists() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        TicketCategory category = new TicketCategory(); // Create a dummy TicketCategory object

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.addTicketCategory(catId, eventId, price);
        });

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void testUpdateTicketPricingWhenCategoryNotExists() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(artists)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.updateTicketPricing(catId, eventId, price);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    public void testUpdateTicketPricingWhenEventNotExists() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        TicketCategory category = new TicketCategory(); // Create a dummy TicketCategory object

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.updateTicketPricing(catId, eventId, price);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    public void testUpdateTicketPricingWhenTicketPricingNotExists() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(artists)
                .build();
        TicketCategory category = TicketCategory.builder()
                .categoryId(catId) // Set the category ID
                .categoryName("General Admission") // Set the category name
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.updateTicketPricing(catId, eventId, price);
        });

        verify(eventRepository, never()).save(any());
    }

    @Test
    public void testUpdateTicketPricingWhenEventDoesNotExists() {
        // Arrange
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> {
            underTest.updateTicketPricing(catId, eventId, price);
        });

        verify(eventRepository, never()).save(any());
    }

//    @Test
//    public void testRemoveTicketCategory() {
//        // Create some test data
//        Integer catId = 1;
//        Integer eventId = 2;
//        Double price = 100.0;
//
//        Set<Artist> artists = new HashSet<>();
//        artists.add(new Artist());
//        artists.add(new Artist());
//
//        Event event = Event.builder()
//                .eventId(eventId)
//                .eventName("Test Event")
//                .eventDescription("A test event description.")
//                .eventDate(LocalDateTime.now())
//                .otherEventInfo("Additional event info.")
//                .eventImage("event-image.jpg")
//                .ticketSaleDate(LocalDateTime.now().plusDays(7))
//                .reviewedBy(new Admin())
//                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
//                .reviewRemarks("Review remarks")
//                .isEnhanced(true)
//                .hasPresale(true)
//                .hasPresaleUsers(true)
//                .organiser(new EventOrganiser())
//                .venue(new Venue())
//                .eventType(new EventType())
//                .artists(artists)
//                .build();
//
//        TicketCategory category = TicketCategory.builder()
//                .categoryId(catId) // Set the category ID
//                .categoryName("General Admission") // Set the category name
//                .build();
//
//        TicketPricing ticketPricing = TicketPricing.builder()
//                .cat(category)
//                .event(event)
//                .price(price)
//                .build();
//
//        // Mock the behavior of your repositories
//        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
//        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));
//        when(ticketPricingRepository.findById(new EventTicketCategoryId(category, event)))
//                .thenReturn(Optional.of(ticketPricing));
//
//        // Call the method you want to test
//        EventDisplayDto result = underTest.removeTicketCategory(catId, eventId);
//
//        // Assertions to check the result
//        assertNotNull(result);
//    }

    @Test
    public void testRemoveTicketCategoryNonExistentCategory() {
        when(ticketCategoryRepository.findById(2)).thenReturn(Optional.empty());

        // Assert that the NonExistentException is thrown for a non-existent category
        assertThrows(NonExistentException.class, () -> {
            underTest.removeTicketCategory(2, 1);
        });
    }

    @Test
    public void testRemoveTicketCategoryNonExistentEvent() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        // Assert that the NonExistentException is thrown for a non-existent event
        assertThrows(NonExistentException.class, () -> {
            underTest.removeTicketCategory(2, 1);
        });
    }

    @Test
    public void testRemoveTicketCategoryNonExistentEventCategoryLink() {
        Integer catId = 1;
        Integer eventId = 2;
        Double price = 100.0;

        Set<Artist> artists = new HashSet<>();
        artists.add(new Artist());
        artists.add(new Artist());

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(artists)
                .build();

        TicketCategory category = TicketCategory.builder()
                .categoryId(catId) // Set the category ID
                .categoryName("General Admission") // Set the category name
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(ticketCategoryRepository.findById(catId)).thenReturn(Optional.of(category));

        // Assert that the NonExistentException is thrown for a non-existent event-category link
        assertThrows(NonExistentException.class, () -> {
            underTest.removeTicketCategory(catId, eventId);
        });
    }

    @Test
    public void testFindArtistForEventSuccess() {
        // Define test data
        Integer eventId = 1;
        Set<Artist> artists = new HashSet<>();

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(artists)
                .build();
        List<Object[]> artistData = new ArrayList<>();

        // Mock the behavior of eventRepository and artistDtoMapper
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.getArtistByEventId(eventId)).thenReturn(artistData);

        // Invoke the method and assert the result
        Set<ArtistDisplayDto> result = underTest.findArtistForEvent(eventId);
        assertNotNull(result);
    }

    @Test
    public void testFindArtistForEventNonExistentEvent() {
        // Define a non-existent event ID
        Integer eventId = 1;

        // Mock the behavior of eventRepository
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Assert that NonExistentException is thrown
        assertThrows(NonExistentException.class, () -> {
            underTest.findArtistForEvent(eventId);
        });
    }

    @Test
    public void testFindAllSectionDetailsForEvent() {
        // Define test data
        Integer eventId = 1;
        Set<Artist> artists = new HashSet<>();

        Event event = Event.builder()
                .eventId(eventId)
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
                .artists(artists)
                .build();
        List<Object[]> ticketDetailsList = new ArrayList<>();

        // Mock the behavior of ticketRepository and sectionDtoMapper
        when(ticketRepository.findAllTicketDetailsBySectionForEvent(event.getEventId())).thenReturn(ticketDetailsList);

        // Invoke the method and assert the result
        List<SectionTicketDetailsDto> result = underTest.findAllSectionDetailsForEvent(event);
        assertNotNull(result);
    }

    @Test
    public void testFindEventsByOrganiserAndEnhancedStatus() {
        // Define test data
        int organiserId = 1;  // Replace with an actual organiser ID
        Boolean enhanced = true;  // Replace with an actual value
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Object[]> expectedEventList = new ArrayList<>();

        // Mock the behavior of eventRepository and eventDtoMapper
        when(eventRepository.findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullAndIsEnhancedAndOrganiserOrganiserIdOrderByEventDateAsc(
                any(), any(LocalDateTime.class), anyBoolean(), anyInt()))
                .thenReturn(Collections.emptyList()); // Replace with actual query result

        // Invoke the method and assert the result
        List<EventHomeDto> result = underTest.findEventsByOrganiserAndEnhancedStatus(organiserId, enhanced);
        assertNotNull(result);
    }
}