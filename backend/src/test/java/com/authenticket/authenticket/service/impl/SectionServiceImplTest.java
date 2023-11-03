package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.section.SectionDtoMapper;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDtoMapper;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private TicketRepository ticketRepository;
    @InjectMocks
    private SectionDtoMapper sectionDtoMapper;

    private SectionServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new SectionServiceImpl(sectionRepository, ticketRepository, sectionDtoMapper);
    }

    @Test
    public void testSaveSection() {
        // Arrange
        Section section = new Section(/* Initialize section properties */);
        when(sectionRepository.save(section)).thenReturn(section);

        // Act
        Section savedSection = underTest.saveSection(section);

        // Assert
        assertEquals(section, savedSection);
        verify(sectionRepository).save(section);
    }

    @Test
    public void testGetCurrentSeatMatrix() {
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

        Set<TicketPricing> ticketPricingSet = new HashSet<>();
        TicketCategory category1 = new TicketCategory();
        category1.setCategoryName("Category 1");
        ticketPricingSet.add(new TicketPricing(category1, event, 20.0));
        event.setTicketPricingSet(ticketPricingSet);

        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(new Venue()) // Set the Venue, you may need to create a helper for Venue as well
                .ticketCategory(new TicketCategory()) // Set the TicketCategory, you may need to create a helper for TicketCategory as well
                .noOfRows(10)
                .noOfSeatsPerRow(20)
                .build();

        List<Ticket> ticketList = new ArrayList<>();
        Ticket dummyTicket = Ticket.builder()
                .ticketId(1)
                .ticketPricing(new TicketPricing())
                .section(section)
                .rowNo(1)
                .seatNo(2)
                .ticketHolder("Georgia")
                .order(new Order())
                .build();
        ticketList.add(dummyTicket);
        // Add some Ticket objects to the list
        when(ticketRepository.findAllByTicketPricingEventEventIdAndSectionSectionId(event.getEventId(), section.getSectionId())).thenReturn(ticketList);

        // Act
        int[][] seatMatrix = underTest.getCurrentSeatMatrix(event, section);

        // Assert
        // Perform assertions on seatMatrix based on your expectations
        verify(ticketRepository).findAllByTicketPricingEventEventIdAndSectionSectionId(event.getEventId(), section.getSectionId());
    }

    @Test
    public void testFindSectionDetail() {
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

        Set<TicketPricing> ticketPricingSet = new HashSet<>();
        TicketCategory category1 = new TicketCategory();
        category1.setCategoryName("Category 1");
        ticketPricingSet.add(new TicketPricing(category1, event, 20.0));
        event.setTicketPricingSet(ticketPricingSet);

        Section section = Section.builder()
                .sectionId("TestSection1")
                .venue(new Venue()) // Set the Venue, you may need to create a helper for Venue as well
                .ticketCategory(new TicketCategory()) // Set the TicketCategory, you may need to create a helper for TicketCategory as well
                .noOfRows(10)
                .noOfSeatsPerRow(20)
                .build();

        List<Object[]> ticketDetailsList = new ArrayList<>();
        // Add some SectionTicketDetailsDto objects to the list
        when(ticketRepository.findTicketDetailsForSection(event.getEventId(), section.getSectionId()))
                .thenReturn(ticketDetailsList);

        // Act
        List<SectionTicketDetailsDto> result = underTest.findSectionDetail(event, section);

        // Assert
        // Perform assertions on the result based on your expectations
        verify(ticketRepository).findTicketDetailsForSection(event.getEventId(), section.getSectionId());
    }
}