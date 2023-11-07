package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.AdminService;
import com.authenticket.authenticket.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AdminService adminService;

    @Autowired
    EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    VenueRepository venueRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventTypeRepository eventTypeRepository;

    @Autowired
    JwtService jwtService;

    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Admin admin;
    private Admin anotherAdmin;

    String token;

    @BeforeAll
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        Optional<Admin> oldAdmin = adminRepository.findByEmail("admin@gmail.com");
        if (oldAdmin.isPresent()) {
            adminRepository.delete(oldAdmin.get());
        }
        String encodedPassword = new BCryptPasswordEncoder().encode("password");

        // ID is auto set to 1 when saving so changing this adminId is pointless
        admin = Admin.builder().adminId(1).password(encodedPassword).email("admin@gmail.com").name("admin").build();
        adminRepository.saveAndFlush(admin);
        // ID is auto set to 2 when saving so changing this adminId is pointless
        anotherAdmin = Admin.builder().adminId(2).password(encodedPassword).email("admin2@gmail.com").name("admin2").build();
        adminRepository.saveAndFlush(anotherAdmin);

        Artist artist = Artist.builder()
                .artistId(1)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();
        Set<Artist> artists = new HashSet<>();
        artists.add(artist);
        artistRepository.saveAndFlush(artist);

        Venue venue = Venue.builder()
                .venueId(1)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();
        venueRepository.saveAndFlush(venue);
        EventOrganiser eventOrganiser = EventOrganiser
                .builder()
                .organiserId(1)
                .name("TestGeorgia")
                .email("testOrg1@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("approved")
                .reviewRemarks("remarks")
                .events(new ArrayList<Event>())
                .build();
        eventOrganiserRepository.saveAndFlush(eventOrganiser);
        EventType eventType = new EventType(1, "newEventType");
        eventTypeRepository.saveAndFlush(eventType);
        Event event = Event.builder()
                .eventId(1)
                .eventName("Test Event")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(admin)
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(eventOrganiser)
                .venue(venue)
                .eventType(eventType)
                .artists(artists)
                .build();
        eventRepository.saveAndFlush(event);

        EventOrganiser organiser2 = EventOrganiser
                .builder()
                .organiserId(2)
                .name("TestGeorgia")
                .email("testOrg2@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("approved")
                .reviewRemarks("remarks")
                .events(new ArrayList<Event>())
                .build();
        eventOrganiserRepository.saveAndFlush(organiser2);

        EventOrganiser organiser3 = EventOrganiser
                .builder()
                .organiserId(3)
                .name("TestGeorgia")
                .email("testOrg3@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("pending")
                .reviewRemarks("remarks")
                .events(new ArrayList<Event>())
                .build();
        eventOrganiserRepository.saveAndFlush(organiser3);

        Event event1 = Event.builder()
                .eventId(2)
                .eventName("Test Event1")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(admin)
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(eventOrganiser)
                .venue(venue)
                .eventType(eventType)
                .artists(artists)
                .build();
        eventRepository.saveAndFlush(event1);
        Event event2 = Event.builder()
                .eventId(3)
                .eventName("Test Event2")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(admin)
                .reviewStatus(Event.ReviewStatus.APPROVED.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(eventOrganiser)
                .venue(venue)
                .eventType(eventType)
                .artists(artists)
                .build();
        eventRepository.saveAndFlush(event2);
        Event event3 = Event.builder()
                .eventId(4)
                .eventName("Test Event3")
                .eventDescription("A test event description.")
                .eventDate(LocalDateTime.now())
                .otherEventInfo("Additional event info.")
                .eventImage("event-image.jpg")
                .ticketSaleDate(LocalDateTime.now().plusDays(7))
                .reviewedBy(admin)
                .reviewStatus(Event.ReviewStatus.PENDING.getStatusValue())
                .reviewRemarks("Review remarks")
                .isEnhanced(true)
                .hasPresale(true)
                .hasPresaleUsers(true)
                .organiser(eventOrganiser)
                .venue(venue)
                .eventType(eventType)
                .artists(artists)
                .build();
        eventRepository.saveAndFlush(event3);

        token = jwtService.generateUserToken(admin);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @AfterAll
    void deleteAdmin(){
        artistRepository.deleteAll();
        venueRepository.deleteAll();
        eventRepository.deleteAll();
        eventOrganiserRepository.deleteAll();
        eventTypeRepository.deleteAll();
        adminRepository.deleteAll();
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/v2/admin";
    }

    @Test
    public void testFindAllAdmin() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort(), HttpMethod.GET, entity, String.class);

        // Check if the response status code is 200 (OK)
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("admin@gmail.com");
    }

    @Test
    public void testFindAdminById() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/1", HttpMethod.GET, entity, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("admin@gmail.com");
    }

    @Test
    public void testUpdateAdmin() throws JsonProcessingException {
        admin.setName("UpdatedAdminName");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(admin), headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort(), HttpMethod.PUT, entity, String.class);

        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Check if the response contains the updated admin's name
        assertThat(responseEntity.getBody()).contains("UpdatedAdminName");
    }

    @Test
    public void testUpdateEventOrganiser() throws JsonProcessingException {
        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/update-organiser?organiserId=1&name=new name", HttpMethod.PUT, entity, String.class);

        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("Event organiser 1 updated successfully.");
        assertThat(responseEntity.getBody()).contains("new name");
    }

    @Test
    public void testUpdateEvent() {

        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/update-event?eventId=1&eventName=new name", HttpMethod.PUT, entity, String.class);

        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("new name");
    }

    @Test
    public void testFindEventOrganisersByReviewStatus() {
        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/event-organiser/review-status/approved", HttpMethod.GET, entity, String.class);
        System.out.println(responseEntity.getBody());
        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("testOrg1@example.com");
        assertThat(responseEntity.getBody()).contains("testOrg2@example.com");
        assertThat(responseEntity.getBody()).doesNotContain("testOrg3@example.com");

    }

    @Test
    public void testFindEventsByReviewStatus() {
        HttpEntity<?> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/event/review-status/approved", HttpMethod.GET, entity, String.class);

        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("Test Event1");
        assertThat(responseEntity.getBody()).contains("Test Event2");
        assertThat(responseEntity.getBody()).doesNotContain("Test Event3");
    }
}
