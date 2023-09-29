package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.section.EventSectionDisplayDto;
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


@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

    private final TicketRepository ticketRepository;

    private final EventRepository eventRepository;

    private final TicketServiceImpl ticketServiceImpl;

    private final TicketCategoryRepository ticketCategoryRepository;


//    private final SectionDtoMapper sectionDtoMapper;

    @Autowired
    public SectionServiceImpl(SectionRepository sectionRepository, TicketRepository ticketRepository, EventRepository eventRepository, TicketServiceImpl ticketServiceImpl, TicketCategoryRepository ticketCategoryRepository) {
        this.sectionRepository = sectionRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.ticketServiceImpl = ticketServiceImpl;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    public List<Ticket> seatAllocate(Integer eventId, Integer sectionId, Integer ticketsToPurchase) {
        //seat allocate, create ticket then order and reassign order number to ticket
        List<Ticket> ticketList = new ArrayList<>();

        //get section details
        Section section = sectionRepository.findById(sectionId).orElse(null);
        Event event = eventRepository.findById(eventId).orElse(null);

        if (section == null) {
            throw new NonExistentException("Section does not exist");
        } else if (event == null) {
            throw new NonExistentException("Event does not exist");
        } else if (ticketsToPurchase < 1 || ticketsToPurchase > 5) {
            throw new IllegalArgumentException("Tickets To Purchase Must Be Between 1 to 5");
        }
        //***check section and venue matches (ensure section is in venue)
        List<Section> sectionList = event.getVenue().getSections();
        if (!sectionList.contains(section)) {
            throw new IllegalArgumentException("Section not connected to event venue");
        }
        //***check for available seats
        if (getNoOfAvailableSeatsBySectionForEvent(event, section) < ticketsToPurchase) {
            throw new IllegalArgumentException("Insufficient seats for event");
        }

        int[][] currentSeatMatrix = getCurrentSeatMatrix(event,section);

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
                    System.out.println("CURRENT COUNT " + count);
                    //find adjacent seats for each count of the seat combination
                    List<Ticket> ticketNewlyCreated = findAdjacentSeatsOf(event, section, count);
                    ticketList.addAll(ticketNewlyCreated);
                }
                //break out of outer loop if tickets for combination is found
                break;

            } catch (Exception e) {
                // Clear ticketList and database if any ticket cannot be found
                ticketServiceImpl.ticketRemoveAll(ticketList);
                ticketList.clear();
                System.out.println(e.getMessage());
            }

        }
        if (ticketList.isEmpty()) {
            throw new NonExistentException("No tickets found");
        }

        //displays updated seat matrix with new seats in console
        getNewSeatMatrix(currentSeatMatrix,ticketList);
        return ticketList;
    }

    public List<EventSectionDisplayDto> getSectionDetailsForEvent(Integer eventId) {
        return null;
    };

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public int[][] getCurrentSeatMatrix(Event event, Section section) {
        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        int[][] seatMatrix = new int[rowNo][colNo];

        //fill up seatMatrix with occupied seats
        List<Ticket> ticketList = ticketRepository.findAllByEventEventIdAndSectionSectionId(event.getEventId(), section.getSectionId());
        for (Ticket ticket : ticketList) {
            Integer ticketRowNo = ticket.getRowNo();
            Integer ticketSeatNo = ticket.getSeatNo();

            seatMatrix[ticketRowNo - 1][ticketSeatNo - 1] = 1; //minus one cause arrays start from index 0
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

    public int[][] getNewSeatMatrix(int[][] currentSeatMatrix, List<Ticket> newTicketsList) {

        //empty seats = 0, occupied seats = 1, newly occupied seats = 2
        for (Ticket ticket : newTicketsList) {
            Integer ticketRowNo = ticket.getRowNo();
            Integer ticketSeatNo = ticket.getSeatNo();

            currentSeatMatrix[ticketRowNo - 1][ticketSeatNo - 1] = 2; //minus one cause arrays start from index 0
        }

        //to display for testing purpose
        for (int i = 0; i < currentSeatMatrix.length; i++) {
            for (int j = 0; j < currentSeatMatrix[0].length; j++) {
                System.out.print(currentSeatMatrix[i][j] + " ");
            }
            System.out.println();

        }

        return currentSeatMatrix;
    }


    public List<Ticket> findAdjacentSeatsOf(Event event, Section section, Integer ticketCount) throws NotFoundException {
        //getting seat matrix with occupancy, 0 = empty seats, 1 = taken
        int[][] currentSeatMatrix = this.getCurrentSeatMatrix(event, section);

        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        TicketCategory ticketCategory = section.getTicketCategory();

        for (int i = 0; i < rowNo; i++) {
            if (ticketRepository.countByEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), rowNo + 1) == colNo) {
                System.out.println(String.format("ROW %d FULL", i));
                continue;
            }
            List<Ticket> ticketList = new ArrayList<>();

            //find all tickets for row
            List<Ticket> ticketsForRow = ticketRepository.findAllByEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), i + 1);

            //initialise all seats for row
            List<Integer> seatsAvailableForRow =new ArrayList<>();
            for(int j = 0; j< colNo;j++){
                seatsAvailableForRow.add(j+1);
            }
            //remove occupied seats from seats available list
            ticketsForRow.forEach(ticket -> seatsAvailableForRow.remove(ticket.getSeatNo()));
            int[] availableSeatsArray = seatsAvailableForRow.stream().mapToInt(Integer::intValue).toArray();

            //find consecutive seats
            List<List<Integer>> consecutiveGroupsOfSeats = findConsecutiveGroups(availableSeatsArray);

            List<Integer> setOfSeats = getRandomSubsetOfSeats(consecutiveGroupsOfSeats,ticketCount);

            if(!setOfSeats.isEmpty()){
                for(Integer seatNo: setOfSeats){
                    Ticket newTicket = new Ticket(null, event, ticketCategory, section, i + 1, seatNo, null, null);
                    ticketList.add(newTicket);
                }
                //save to db
                ticketRepository.saveAll(ticketList);
                return ticketList;
            }


//            for (int j = 0; j < seatsOccupiedForRow.length - 1; j++) {
//                //if there are n number of empty seats in between the occupied seats
//                Integer emptySeatsInBetween = seatsOccupiedForRow[j + 1] - seatsOccupiedForRow[j] -1; //if seat 1 and 5 is occupied, 5-1=4 empty seats which is incorrect, which is why we -1 again
//                if (emptySeatsInBetween >= ticketCount) {
//                    Integer upperSeatNo = seatsOccupiedForRow[j] + ticketCount;
//                    for (int seatNo = seatsOccupiedForRow[j]; seatNo < seatsOccupiedForRow[j + 1]; seatNo++) {
//                        //add ticket to list
//                        Ticket newTicket = new Ticket(null, event, ticketCategory, section, i + 1, j + 1, null, null);
//                        ticketList.add(newTicket);
//                    }
//                    //save to db
//                    ticketRepository.saveAll(ticketList);
//                    return ticketList
//                }
//            }

        }


//        for(int i = 0; i < rowNo;i++){
//            List<Ticket> ticketList = new ArrayList<>();
//            //check if row is full alr
//            if(ticketRepository.countByEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), rowNo+1) == colNo){
//                System.out.println(String.format("ROW %d FULL", i));
//                continue;
//            }
//            for(int j = 0; j < colNo;j++){
//
//                //empty seat
//                if(currentSeatMatrix[i][j] == 0){
//                    Ticket newTicket = new Ticket(null,event,ticketCategory,section,i+1,j+1,null,null);
//                    ticketList.add(newTicket);
//                    //adjacent seats of size found successfully
//                    if(ticketList.size() == ticketCount){
//                        //save tickets in db
//                        ticketRepository.saveAll(ticketList);
//                        return ticketList;
//                    }
//                } else{
//                    //if occupied seat found clear and restart the count
//                    ticketList.clear();
//                }
//            }
//        }

        throw new NotFoundException(String.format("Adjacent seats of size not found %d", ticketCount));
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

    public Integer getNoOfAvailableSeatsBySectionForEvent(Event event, Section section) {
        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();
        Integer totalSeats = rowNo * colNo;
        return totalSeats - ticketRepository.countByEventEventIdAndSectionSectionId(event.getEventId(), section.getSectionId());
    };

    public static List<List<Integer>> findConsecutiveGroups(int[] nums) {
        List<List<Integer>> consecutiveGroups = new ArrayList<>();
        if (nums.length == 0) {
            return consecutiveGroups;
        }

        List<Integer> currentGroup = new ArrayList<>();
        currentGroup.add(nums[0]);

        for (int i = 1; i < nums.length; i++) {
            // Check if the current number is consecutive to the previous one
            if (nums[i] == nums[i - 1] + 1) {
                currentGroup.add(nums[i]);
            } else {
                // If not consecutive, start a new group
                consecutiveGroups.add(currentGroup);
                currentGroup = new ArrayList<>();
                currentGroup.add(nums[i]);
            }
        }

        // Add the last group
        consecutiveGroups.add(currentGroup);

        return consecutiveGroups;
    }

    public static List<Integer> getRandomSubsetOfSeats(List<List<Integer>> consecutiveGroups, int n) {
        List<List<Integer>> validGroups = new ArrayList<>();

        // Filter groups with length >= n
        for (List<Integer> group : consecutiveGroups) {
            if (group.size() >= n) {
                validGroups.add(group);
            }
        }

        if (validGroups.isEmpty()) {
            return new ArrayList<>(); // No valid groups found
        }

        Random random = new Random();
        List<Integer> selectedGroup = validGroups.get(random.nextInt(validGroups.size())); // Randomly select a group
        int startIndex = random.nextInt(selectedGroup.size() - n + 1); // Random starting index
        List<Integer> selectedSubset = new ArrayList<>();

        // Add 'n' consecutive numbers to the selectedSubset
        for (int i = startIndex; i < startIndex + n; i++) {
            selectedSubset.add(selectedGroup.get(i));
        }

        return selectedSubset;
    }



}
