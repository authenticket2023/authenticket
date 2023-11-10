package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.section.SectionDtoMapper;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class provides the implementation of the `SectionService` interface, which manages event sections and seat assignments.
 */

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    private final SectionDtoMapper sectionDtoMapper;


    private final TicketRepository ticketRepository;

    @Autowired
    public SectionServiceImpl(SectionRepository sectionRepository, TicketRepository ticketRepository, SectionDtoMapper sectionDtoMapper) {
        this.sectionRepository = sectionRepository;
        this.ticketRepository = ticketRepository;
        this.sectionDtoMapper = sectionDtoMapper;

    }

    /**
     * Save a section.
     *
     * @param section The section to save.
     * @return The saved section.
     */
    @Override
    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    /**
     * Get the current seat matrix for a specific event and section.
     *
     * @param event   The event for which to get the seat matrix.
     * @param section The section for which to get the seat matrix.
     * @return A two-dimensional array representing the seat matrix where 0 indicates an unoccupied seat and 1 indicates an occupied seat.
     */
    @Override
    public int[][] getCurrentSeatMatrix(Event event, Section section) {
        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        int[][] seatMatrix = new int[rowNo][colNo];

        //fill up seatMatrix with occupied seats
        List<Ticket> ticketList = ticketRepository.findAllByTicketPricingEventEventIdAndSectionSectionId(event.getEventId(), section.getSectionId());
        for (Ticket ticket : ticketList) {
            Integer ticketRowNo = ticket.getRowNo();
            Integer ticketSeatNo = ticket.getSeatNo();

            seatMatrix[ticketRowNo - 1][ticketSeatNo - 1] = 1; //minus one cause arrays start from index 0
        }
        System.out.println("----------" + event.getEventId() + " " + section.getSectionId() + "----------");
        //to display for testing purpose
        for (int i = 0; i < rowNo; i++) {
            for (int j = 0; j < colNo; j++) {
                System.out.print(seatMatrix[i][j] + " ");
            }
            System.out.println();

        }
        System.out.println();

        return seatMatrix;
    }

    /**
     * Find section ticket details for a specific event and section.
     *
     * @param event   The event for which to retrieve section ticket details.
     * @param section The section for which to retrieve ticket details.
     * @return A list of `SectionTicketDetailsDto` containing ticket details for the given event and section.
     */

    @Override
    public List<SectionTicketDetailsDto> findSectionDetail(Event event, Section section){
        List<SectionTicketDetailsDto> sectionTicketDetailsDto = sectionDtoMapper.mapSectionTicketDetailsDto(ticketRepository.findTicketDetailsForSection(event.getEventId(), section.getSectionId()));
        return sectionTicketDetailsDto;
    };
}
