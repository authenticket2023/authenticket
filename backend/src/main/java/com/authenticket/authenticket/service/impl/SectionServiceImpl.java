package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.section.EventSectionDisplayDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
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

    public int[][] getCurrentSeatMatrix(Integer eventId, Section section) {
        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        int[][] seatMatrix = new int[rowNo][colNo];

        //fill up seatMatrix with occupied seats
        List<Ticket> ticketList = ticketRepository.findAllByEvent_EventId(eventId);
        for (Ticket ticket : ticketList) {
            Integer ticketRowNo = ticket.getRowNo();
            Integer ticketSeatNo = ticket.getSeatNo();

            seatMatrix[ticketRowNo - 1][ticketSeatNo - 1] = 1; //negative one cause arrays start from index 0
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

    public List<Ticket> seatAllocate(Integer eventId, Integer sectionId, Integer ticketsToPurchase) {
        //seat allocate, create ticket then order and reassign order number to ticket
        List<Ticket> ticketList = new ArrayList<>();

        //get section details
        Section section = sectionRepository.findById(sectionId).orElse(null);

        if (section == null) {
            throw new NonExistentException("Section does not exist");
        } else if (ticketsToPurchase < 1 || ticketsToPurchase > 5) {
            throw new IllegalArgumentException("Tickets To Purchase Must Be Between 1 to 5");
        }

        //get seat combination rank
        String[] seatCombinationRank = getSeatCombinationRank(ticketsToPurchase);

        //find adjacent seats of
        //loop through each combi
        for (String combination : seatCombinationRank) {
            //convert combi into arrays of tickets (3,1,1) etc for tickets of 5
            Integer[] currentCombi = Arrays.stream(combination.split(","))
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);
            try {
                for (Integer count : currentCombi) {
                    System.out.println(count);

                    List<Ticket> ticketNewlyCreated = findAdjacentSeatsOf(eventId, section, count);
                    ticketList.addAll(ticketNewlyCreated);
                }
                break;

            } catch (Exception e) {
                // Clear ticketList if any ticket cannot be found
                ticketList.clear();
            }

        }


        return ticketList;
    }

    public void seatUnallocate(Integer orderId) {

    }

    public List<Ticket> findAdjacentSeatsOf(Integer eventId, Section section, Integer ticketCount) throws NotFoundException {
        //getting seat matrix with occupancy, null = empty seats, 1 = taken, 2 = reserved
        int[][] currentSeatMatrix = this.getCurrentSeatMatrix(eventId, section);

        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();
        Integer count = 0;

        for(int i = 0; i < rowNo;i++){
            for(int j = 0; j < colNo;j++){
                
            }
        }

        return null;
    }


    public String[] getSeatCombinationRank(Integer ticketCount) {
        String[] seatCombiRank = null;
        if (ticketCount < 1 || ticketCount > 5) {
            return null;
        }
        if (ticketCount == 5) {
            seatCombiRank = new String[]{"5", "3,2", "2,2,1", "4,1", "3,1,1", "2,1,1,1", "1,1,1,1,1"};
        } else if (ticketCount == 4) {
            seatCombiRank = new String[]{"4", "2,2", "2,1,1", "1,1,1,1"};
        } else if (ticketCount == 3) {
            seatCombiRank = new String[]{"3", "2,1", "1,1,1"};
        } else if (ticketCount == 2) {
            seatCombiRank = new String[]{"2", "1,1"};
        } else if (ticketCount == 1) {
            seatCombiRank = new String[]{"1"};
        }

        return seatCombiRank;
    }


}
