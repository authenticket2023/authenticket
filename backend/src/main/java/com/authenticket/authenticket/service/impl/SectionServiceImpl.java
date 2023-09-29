package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.section.EventSectionDisplayDto;
import com.authenticket.authenticket.dto.section.SeatAllocationDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    private final TicketRepository ticketRepository;

//    private final SectionDtoMapper sectionDtoMapper;

    @Autowired
    public SectionServiceImpl(SectionRepository sectionRepository, TicketRepository ticketRepository) {
        this.sectionRepository = sectionRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<EventSectionDisplayDto> getSectionDetailsForEvent(Integer eventId) {
        return null;
    }

    ;

    public Section saveSection(Section section) {

        return sectionRepository.save(section);
    }

    public List<SeatAllocationDto> seatAllocation(Integer eventId, Integer sectionId, Integer ticketsToPurchase) {
        List<SeatAllocationDto> seatAllocationDtoList = new ArrayList<>();

        //get section details
        Section section = sectionRepository.findById(sectionId).orElse(null);
//        Event event = eve.findById(sectionId).orElse(null);

        if (section == null) {
            throw new NonExistentException("Section does not exist");
        }  else if(ticketsToPurchase < 1 || ticketsToPurchase > 5){
            throw new IllegalArgumentException("Tickets To Purchase Must Be Between 1 to 5");
        }
//        else if(event ==null){
//            throw new NonExistentException("Event does not exist");
//        }

        //getting seat matrix with occupancy, null = empty seats, 1 = taken
        int[][] seatMatrix = this.getSeatMatrix(eventId, section);

        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        //try to find adjacent seats and if not possible find seat allocations with different ticket count

        return seatAllocationDtoList;
    }

    public int[][] getSeatMatrix(Integer eventId, Section section) {
        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        int[][] seatMatrix = new int[rowNo][colNo];

        //fill up seatMatrix with occupied seats
        List<Ticket> ticketList = ticketRepository.findAllByTicketPricing_Event_EventId(eventId);
        for (Ticket ticket : ticketList) {
            Integer ticketRowNo = ticket.getRowNo();
            Integer ticketSeatNo = ticket.getSeatNo();

            seatMatrix[ticketRowNo-1][ticketSeatNo-1] = 1; //negative one cause arrays start from index 0
        }

        //to display for testing purpose
        for (int i = 0; i < rowNo; i++) {
            for (int j = 0; j < colNo; j++) {
                System.out.print(seatMatrix[i][j] + " ");
            }
            System.out.println();

        }

        return seatMatrix;
    }


}
