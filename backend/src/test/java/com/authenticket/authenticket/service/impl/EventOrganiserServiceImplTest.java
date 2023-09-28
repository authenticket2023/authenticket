package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventOrganiserServiceImplTest {

    @Mock
    private EventOrganiserRepository eventOrganiserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AmazonS3Service amazonS3Service;
    @InjectMocks
    private EmailServiceImpl emailService;

    private EventOrganiserServiceImpl underTest;

    @BeforeEach
    void setUp(){
        AdminDtoMapper adminDtoMapper = new AdminDtoMapper(passwordEncoder);
        EventOrganiserDtoMapper eventOrganiserDtoMapper = new EventOrganiserDtoMapper(passwordEncoder, adminDtoMapper);
        underTest = new EventOrganiserServiceImpl(
                eventOrganiserRepository,
                eventOrganiserDtoMapper,
                emailService,
                amazonS3Service,
                passwordEncoder
                );
    }

    @Test
    void testFindAllEventOrganisers() {
        List<EventOrganiser> mockOrgList = new ArrayList<>();
        // Mock the behavior of eventOrganiserRepository.findAll to return a list of EventOrganisers
        when(eventOrganiserRepository.findAll()).thenReturn(mockOrgList);

        List<EventOrganiserDisplayDto> result = underTest.findAllEventOrganisers();

        // Assert that the result is not null and contains the expected values
        assertNotNull(result);
        assertEquals(mockOrgList.size(), result.size());
    }

    @Test
    void testFindAllEventsByOrganiser() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();

        // Mock the behavior of eventOrganiserRepository.findById to return an Optional EventOrganiser
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.of(eventOrg));

        List<Event> result = underTest.findAllEventsByOrganiser(organiserId);

        // Assert that the result is not null and contains the expected values
        assertEquals(eventList,result);
    }

    @Test
    void testFindAllEventsByOrganiserWhenOrganiserDoesNotExist() {
        Integer organiserId = -1;

        // Mock the behavior of eventOrganiserRepository.findById to return an Optional EventOrganiser
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.empty());

        List<Event> result = underTest.findAllEventsByOrganiser(organiserId);

        // Assert that the result is not null and contains the expected values
        assertEquals(new ArrayList<>(),result);
    }

    @Test
    void testFindEventOrganisersByReviewStatus() {
        List<EventOrganiser> eventOrgList = new ArrayList<>();
        // Mock the behavior of eventOrganiserRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc to return a list of EventOrganisers
        when(eventOrganiserRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(any(String.class))).thenReturn(eventOrgList);

        List<EventOrganiserDisplayDto> result = underTest.findEventOrganisersByReviewStatus("status");

        // Assert that the result is not null and contains the expected values
        assertNotNull(result);
        assertEquals(eventOrgList, result);
    }

    @Test
    void testFindEventOrganiserById(){
        // Arrange
        Integer orgId = 1;
        EventOrganiser eventOrg = new EventOrganiser();
        when(eventOrganiserRepository.findById(orgId)).thenReturn(Optional.of(eventOrg));

        // Act
        Optional<EventOrganiserDisplayDto> result = underTest.findOrganiserById(orgId);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void testSaveEventOrganiser() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();

        // Mock the behavior of eventOrganiserRepository.save to return a saved EventOrganiser
        when(eventOrganiserRepository.save(any(EventOrganiser.class))).thenReturn(eventOrg);

        EventOrganiser savedEventOrganiser = underTest.saveEventOrganiser(eventOrg);

        // Assert that the savedEventOrganiser is not null and has the expected values
        assertNotNull(savedEventOrganiser);
        assertEquals(savedEventOrganiser, eventOrg);
        // Add more assertions as needed
    }

    @Test
    void testUpdateEventOrganiser() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();
        // Mock the behavior of eventOrganiserRepository.findById to return an Optional EventOrganiser
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.of(eventOrg));

        // Mock the behavior of eventOrganiserRepository.save to return the updated EventOrganiser
        when(eventOrganiserRepository.save(any(EventOrganiser.class))).thenReturn(eventOrg);

        EventOrganiserUpdateDto eventOrganiserUpdateDto = new EventOrganiserUpdateDto(
                organiserId,
                eventOrg.getName(),
                eventOrg.getDescription(),
                eventOrg.getPassword(),
                eventOrg.getEnabled(),
                eventOrg.getReviewStatus(),
                eventOrg.getReviewRemarks(),
                eventOrg.getAdmin());
        EventOrganiser result = underTest.updateEventOrganiser(eventOrganiserUpdateDto);

        // Assert that the result is not null and contains the expected values
        assertNotNull(result);
        // Add more assertions as needed
    }

    @Test
    void testUpdateEventOrganiserIfNonExistent() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();
        // Mock the behavior of eventOrganiserRepository.findById to return an empty Optional
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.empty());

        EventOrganiserUpdateDto eventOrganiserUpdateDto = new EventOrganiserUpdateDto(
                organiserId,
                eventOrg.getName(),
                eventOrg.getDescription(),
                eventOrg.getPassword(),
                eventOrg.getEnabled(),
                eventOrg.getReviewStatus(),
                eventOrg.getReviewRemarks(),
                eventOrg.getAdmin());

        // Assert that a NonExistentException is thrown
        assertThrows(NonExistentException.class, () -> underTest.updateEventOrganiser(eventOrganiserUpdateDto));
    }
    @Test
    void testUpdateEventOrganiserImageIfOrganiserExists() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage("exist")
                .enabled(true)
                .admin(null)
                .reviewStatus("approved")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();
        // Mock the behavior of eventOrganiserRepository.findById to return an Optional EventOrganiser
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.of(eventOrg));

        // Mock the behavior of eventOrganiserRepository.save to return the updated EventOrganiser
        when(eventOrganiserRepository.save(any(EventOrganiser.class))).thenReturn(eventOrg);

        EventOrganiser result = underTest.updateEventOrganiserImage(organiserId, "exist");

        // Assert that the result is not null and contains the expected values
        assertNotNull(result);
        // Add more assertions as needed
    }

    @Test
    void testUpdateEventOrganiserImageIfOrganiserDoesNotExist() {
        Integer organiserId = -1;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("approved")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();

        EventOrganiser result = underTest.updateEventOrganiserImage(organiserId, null);

        // Assert that the result is not null and contains the expected values
        assertNull(result);
        // Add more assertions as needed
    }

    @Test
    void testDeleteEventOrganiser() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();

        // Mock the behavior of eventOrganiserRepository.findById to return an Optional EventOrganiser
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.of(eventOrg));

        String result = underTest.deleteEventOrganiser(organiserId);

        // Assert that the result is not null and contains the expected value
        assertNotNull(result);
        verify(eventOrganiserRepository).save(eventOrg);
    }

    @Test
    void testDeleteEventOrganiserWhenAlreadyDeleted() {
        Integer organiserId = 99;
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(organiserId)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();
        eventOrg.setDeletedAt(LocalDateTime.now());
        // Mock the behavior of eventOrganiserRepository.findById to return an Optional EventOrganiser
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.of(eventOrg));
        String result = underTest.deleteEventOrganiser(organiserId);
        // Assert that a already deleted error message sent
        assertEquals(String.format("Event Organiser %d is already deleted.", organiserId), result);
    }

    @Test
    void testDeleteEventOrganiserWhenNonExistent() {
        Integer organiserId = -1;
        // Mock the behavior of eventOrganiserRepository.findById to return an empty Optional
        when(eventOrganiserRepository.findById(organiserId)).thenReturn(Optional.empty());

        // Assert that a NonExistentException is thrown
        assertThrows(NonExistentException.class, () -> underTest.deleteEventOrganiser(organiserId));
    }
}