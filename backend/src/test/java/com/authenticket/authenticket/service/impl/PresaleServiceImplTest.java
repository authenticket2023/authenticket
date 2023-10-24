package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.PresaleInterestRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresaleServiceImplTest {

    @InjectMocks
    private PresaleServiceImpl underTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PresaleInterestRepository presaleInterestRepository;

    @Mock
    private EmailService emailService;

    @Value("${authenticket.frontend-production-url}")
    private String apiUrl;

    @BeforeEach
    void setUp() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // Initialize the mailSender as needed
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl(mailSender);
        underTest = new PresaleServiceImpl(userRepository, eventRepository, presaleInterestRepository, venueRepository, emailServiceImpl);
    }

    @Test
    public void testFindPresaleInterestByID() {
        EventUserId eventUserId = new EventUserId(new User(), new Event());
        when(presaleInterestRepository.findById(eventUserId)).thenReturn(Optional.of(new PresaleInterest()));

        Optional<PresaleInterest> result = underTest.findPresaleInterestByID(eventUserId);

        assertTrue(result.isPresent());
    }

    @Test
    public void testExistsById() {
        EventUserId eventUserId = new EventUserId(new User(), new Event());
        when(presaleInterestRepository.findById(eventUserId)).thenReturn(Optional.of(new PresaleInterest()));

        boolean exists = underTest.existsById(eventUserId);

        assertTrue(exists);
    }

    @Test
    public void testFindUsersInterestedByEvent() {
        Event event = new Event();
        when(presaleInterestRepository.findAllByEvent(event)).thenReturn(Collections.singletonList(new PresaleInterest()));

        List<User> users = underTest.findUsersInterestedByEvent(event);

        assertFalse(users.isEmpty());
    }

    @Test
    public void testFindEventsByUser() {
        User user = new User();
        when(presaleInterestRepository.findAllByUser(user)).thenReturn(Collections.singletonList(new PresaleInterest()));

        List<Event> events = underTest.findEventsByUser(user);

        assertFalse(events.isEmpty());
    }

    @Test
    public void testFindUsersSelectedForEvent() {
        Event event = new Event();
        when(presaleInterestRepository.findAllByEventAndIsSelected(event, true))
                .thenReturn(Collections.singletonList(new PresaleInterest()));

        List<User> users = underTest.findUsersSelectedForEvent(event, true);

        assertFalse(users.isEmpty());
    }

    @Test
    public void testSetPresaleInterest_Success() {
        User user = User.builder()
                .userId(99)
                .name("UpdatedGeorgia")
                .email("test@example.com")
                .password("update")
                .build();

        Event event = Event.builder()
                .eventId(99)
                .eventName("Test Event")
                .build();

        PresaleInterest presaleInterest = PresaleInterest.builder()
                .user(user)
                .event(event)
                .isSelected(false)
                .emailed(false)
                .build();

        EventUserId eventUserId = new EventUserId(user, event);

        assertDoesNotThrow(() -> underTest.setPresaleInterest(user, event, false, false));
    }

    @Test
    public void testSelectPresaleUsersForEvent() {
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
        List<User> users = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        users.add(user1);
        users.add(user2);

        when(presaleInterestRepository.findAllByEvent(event)).thenReturn(Arrays.asList(new PresaleInterest(), new PresaleInterest()));

        assertDoesNotThrow(() -> underTest.selectPresaleUsersForEvent(event));
    }

    @Test
    public void testSendScheduledEmails() {
        User user = User.builder()
                .userId(99)
                .name("UpdatedGeorgia")
                .email("test@example.com")
                .password("update")
                .build();
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
        EventUserId eventUserId = new EventUserId(user, event);

        PresaleInterest presaleInterest = PresaleInterest.builder()
                .user(user)
                .event(event)
                .isSelected(true)
                .emailed(false)
                .build();

        List<PresaleInterest> presaleInterestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            presaleInterestList.add(presaleInterest);
        }

        when(presaleInterestRepository.findAllByIsSelectedTrueAndEmailedFalse()).thenReturn(presaleInterestList);

        assertThrows(IllegalStateException.class, () ->underTest.sendScheduledEmails());
    }
}
