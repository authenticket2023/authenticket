package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDtoMapper;
import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventTypeServiceImplTest {

    @Mock
    private EventTypeRepository eventTypeRepository;

    private EventTypeServiceImpl underTest;

    @BeforeEach
    void setUp(){
        underTest = new EventTypeServiceImpl(eventTypeRepository);
    }
    @Test
    void testFindAllEventType() {
        // Arrange: Mock data
        List<EventType> eventTypes = Arrays.asList(
                new EventType(1, "eventType1"),
                new EventType(2, "eventType2")
        );

        // Mock repository behavior
        when(eventTypeRepository.findAll()).thenReturn(eventTypes);

        //Act: Call the method being tested
        List<EventType> result = underTest.findAllEventType();

        // Assertions
        verify(eventTypeRepository).findAll();
        assertEquals(eventTypes.size(), result.size());
    }

    @Test
    void testSaveEventType() {
        // Arrange
        EventType newEventType = new EventType(99, "newEventType");
        when(eventTypeRepository.save(any())).thenReturn(new EventType());

        // Act
        EventType result = underTest.saveEventType(newEventType);

        // Assertions
        assertNotNull(result);
    }
}