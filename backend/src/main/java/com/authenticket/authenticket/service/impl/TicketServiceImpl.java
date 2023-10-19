package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.PresaleService;
import com.authenticket.authenticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketCategoryRepository ticketCategoryRepository;


    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final TicketPricingRepository ticketPricingRepository;

    private final SectionRepository sectionRepository;


    private final TicketRepository ticketRepository;

    private final OrderRepository orderRepository;
    private final TicketDisplayDtoMapper ticketDisplayDtoMapper;
    private final VenueRepository venueRepository;

    @Autowired

    public TicketServiceImpl(TicketCategoryRepository ticketCategoryRepository,
                             UserRepository userRepository,
                             EventRepository eventRepository,
                             TicketRepository ticketRepository,
                             TicketDisplayDtoMapper ticketDisplayDtoMapper,
                             SectionRepository sectionRepository,
                             TicketPricingRepository ticketPricingRepository,
                             OrderRepository orderRepository,
                             VenueRepository venueRepository) {
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.ticketDisplayDtoMapper = ticketDisplayDtoMapper;
        this.sectionRepository = sectionRepository;
        this.ticketPricingRepository = ticketPricingRepository;
        this.venueRepository = venueRepository;
    }

    @Override
    public List<TicketDisplayDto> findAllTicket() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketDisplayDtoMapper)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDisplayDto findTicketById(Integer ticketId) {
        Optional<TicketDisplayDto> ticketDisplayDtoOptional = ticketRepository.findById(ticketId).map(ticketDisplayDtoMapper);
        if (ticketDisplayDtoOptional.isPresent()) {
            return ticketDisplayDtoOptional.get();
        }

        throw new ApiRequestException("Ticket not found");
    }

    @Override
    public List<TicketDisplayDto> findAllByOrderId(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return ticketRepository.findAllByOrder(order)
                    .stream()
                    .map(ticketDisplayDtoMapper)
                    .collect(Collectors.toList());
        }
        throw new ApiRequestException("Order not found");
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

//    public Ticket updateTicket(TicketUpdateDto ticketUpdateDto) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketUpdateDto.ticketId());
//
//        if (ticketOptional.isPresent()) {
//            Ticket existingTicket = ticketOptional.get();
//            ticketDisplayDtoMapper.update(ticketUpdateDto, existingTicket);
//            ticketRepository.save(existingTicket);
//            return existingTicket;
//        }
//
//        throw new NonExistentException("Error Updating Ticket: Ticket not found");
//    }
//
//
//    public void deleteTicket(Integer ticketId) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//
//        if (ticketOptional.isPresent()) {
//            Ticket ticket = ticketOptional.get();
//            if (ticket.getDeletedAt() != null) {
//                throw new AlreadyDeletedException("Ticket already deleted");
//            }
//
//            ticket.setDeletedAt(LocalDateTime.now());
//            ticketRepository.save(ticket);
//        } else {
//            throw new NonExistentException("Ticket does not exist");
//        }
//    }
//
//    public void removeTicket(Integer ticketId) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//
//        if (ticketOptional.isPresent()) {
//            ticketRepository.deleteById(ticketId);
//        } else {
//            throw new NonExistentException("Ticket does not exist");
//        }
//    }

    @Override
    public List<Ticket> allocateSeats(Integer eventId, String sectionId, Integer ticketsToPurchase) {
        //seat allocate, create ticket then order and reassign order number to ticket
        List<Ticket> ticketList = new ArrayList<>();


        //get section details
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new NonExistentException("Event does not exist");
        }
        Venue venue = event.getVenue();
        VenueSectionId venueSectionId = new VenueSectionId(venue, sectionId);
        Section section = sectionRepository.findById(venueSectionId).orElse(null);
        if (section == null) {
            throw new NonExistentException("Section does not exist");
        } else if (ticketsToPurchase < 1 || ticketsToPurchase > 5) {
            throw new IllegalArgumentException("Tickets To Purchase Must Be Between 1 to 5");
        }
        //check section and venue matches (ensure section is in venue)
        List<Section> sectionList = event.getVenue().getSections();
        if (!sectionList.contains(section)) {
            throw new IllegalArgumentException("Section not connected to event venue");
        }
        //check for available seats
        if (getNoOfAvailableSeatsBySectionForEvent(event, section) < ticketsToPurchase) {
            throw new IllegalArgumentException("Insufficient seats for event");
        }

        int[][] currentSeatMatrix = getCurrentSeatMatrix(event, section);

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
                    //find adjacent seats for each count of the seat combination
                    List<Ticket> ticketNewlyCreated = findConsecutiveSeatsOf(event, section, count);
                    ticketList.addAll(ticketNewlyCreated);
                }
                //break out of outer loop if tickets for combination is found
                break;

            } catch (NotFoundException e) {
                // Clear ticketList and database if any ticket cannot be found
                ticketRepository.deleteAll(ticketList);
                ticketList.clear();
            } catch (Exception e) {
                // Clear ticketList and database if got any error first
                ticketRepository.deleteAll(ticketList);
                ticketList.clear();
                e.printStackTrace();
                throw e;
            }

        }
        if (ticketList.isEmpty()) {
            throw new NonExistentException("No tickets found");
        }

        //displays updated seat matrix with new seats in console
        getNewSeatMatrix(currentSeatMatrix, ticketList);
        return ticketList;
    }

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

    @Override
    public int[][] getNewSeatMatrix(int[][] currentSeatMatrix, List<Ticket> newTicketsList) {

        //empty seats = 0, occupied seats = 1, newly occupied seats = 2
        for (Ticket ticket : newTicketsList) {
            Integer ticketRowNo = ticket.getRowNo();
            Integer ticketSeatNo = ticket.getSeatNo();

            currentSeatMatrix[ticketRowNo - 1][ticketSeatNo - 1] = 2; //minus one cause arrays start from index 0
        }

        //to display for testing purpose
        System.out.println("---" + "ASSIGNED SEATS +" + newTicketsList.size() + "---");

        for (int i = 0; i < currentSeatMatrix.length; i++) {
            for (int j = 0; j < currentSeatMatrix[0].length; j++) {
                System.out.print(currentSeatMatrix[i][j] + " ");
            }
            System.out.println();

        }
        System.out.println();

        return currentSeatMatrix;
    }

    @Override
    public List<Ticket> findConsecutiveSeatsOf(Event event, Section section, Integer ticketCount) throws NotFoundException, NonExistentException {
        //getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();

        TicketCategory ticketCategory = section.getTicketCategory();

        EventTicketCategoryId id = new EventTicketCategoryId(ticketCategory, event);
        Optional<TicketPricing> optionalTicketPricing = ticketPricingRepository.findById(id);
        if (optionalTicketPricing.isEmpty()) {
            throw new NonExistentException("Ticket Pricing", id);
        }

        TicketPricing ticketPricing = optionalTicketPricing.get();


        for (int i = 0; i < rowNo; i++) {
            if (ticketRepository.countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), rowNo + 1) == colNo) {
                System.out.println(String.format("ROW %d FULL", i));
                continue;
            }
            List<Ticket> ticketList = new ArrayList<>();

            //find all tickets for row
            List<Ticket> ticketsForRow = ticketRepository.findAllByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), i + 1);

            //initialise all seats for row
            List<Integer> seatsAvailableForRow = new ArrayList<>();
            for (int j = 0; j < colNo; j++) {
                seatsAvailableForRow.add(j + 1);
            }
            //remove occupied seats from seats available list
            ticketsForRow.forEach(ticket -> seatsAvailableForRow.remove(ticket.getSeatNo()));
            int[] availableSeatsArrayForRow = seatsAvailableForRow.stream().mapToInt(Integer::intValue).toArray(); //for the row

            //find consecutive seats
            List<List<Integer>> consecutiveGroupsOfSeats = findConsecutiveGroups(availableSeatsArrayForRow);

            //find subset of consecutive seats and randomising it
            List<Integer> setOfSeats = getRandomSubsetOfSeats(consecutiveGroupsOfSeats, ticketCount);

            if (!setOfSeats.isEmpty()) {
                for (Integer seatNo : setOfSeats) {
                    Ticket newTicket = new Ticket(null, ticketPricing, section, i + 1, seatNo, null, null);
                    ticketList.add(newTicket);
                }
                //save to db
                ticketRepository.saveAll(ticketList);
                return ticketList;
            }
        }

        throw new NotFoundException(String.format("Consecutive seats of size %d not found ", ticketCount));

    }

    @Override
    public String[] getSeatCombinationRank(Integer ticketCount) {
        String[] seatCombiRank = null;
        if (ticketCount < 1 || ticketCount > 5) {
            return null;
        }
        if (ticketCount == 5) {
            seatCombiRank = new String[]{"5", "3,2", "2,2,1", "4,1", "3,1,1", "2,1,1,1", "1,1,1,1,1"};
        } else if (ticketCount == 4) {
            seatCombiRank = new String[]{"4", "2,2", "3,1", "2,1,1", "1,1,1,1"};
        } else if (ticketCount == 3) {
            seatCombiRank = new String[]{"3", "2,1", "1,1,1"};
        } else if (ticketCount == 2) {
            seatCombiRank = new String[]{"2", "1,1"};
        } else if (ticketCount == 1) {
            seatCombiRank = new String[]{"1"};
        }

        return seatCombiRank;
    }

    @Override
    public List<List<Integer>> findConsecutiveGroups(int[] availableSeatsArrayForRow) {
        List<List<Integer>> consecutiveGroups = new ArrayList<>();
        if (availableSeatsArrayForRow.length == 0) {
            return consecutiveGroups;
        }

        List<Integer> currentGroup = new ArrayList<>();
        currentGroup.add(availableSeatsArrayForRow[0]);

        for (int i = 1; i < availableSeatsArrayForRow.length; i++) {
            // Check if the current number is consecutive to the previous one
            if (availableSeatsArrayForRow[i] == availableSeatsArrayForRow[i - 1] + 1) {
                currentGroup.add(availableSeatsArrayForRow[i]);
            } else {
                // If not consecutive, start a new group
                consecutiveGroups.add(currentGroup);
                currentGroup = new ArrayList<>();
                currentGroup.add(availableSeatsArrayForRow[i]);
            }
        }

        // Add the last group
        consecutiveGroups.add(currentGroup);

        return consecutiveGroups;
    }

    @Override
    public List<Integer> getRandomSubsetOfSeats(List<List<Integer>> consecutiveGroupsOfSeats, int n) {
        List<List<Integer>> validGroups = new ArrayList<>();

        // Filter groups with length >= n
        for (List<Integer> group : consecutiveGroupsOfSeats) {
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

    @Override
    public Integer getNoOfAvailableSeatsBySectionForEvent(Event event, Section section) {
        if (ticketRepository.findNoOfAvailableTicketsBySectionAndEvent(event.getEventId(), section.getSectionId()) == null) {
            return section.getNoOfRows() * section.getNoOfSeatsPerRow();
        }
        return ticketRepository.findNoOfAvailableTicketsBySectionAndEvent(event.getEventId(), section.getSectionId());

    }

    @Override
    public void removeAllTickets(List<Integer> ticketIdList) {
        List<Ticket> ticketList = ticketRepository.findAllById(ticketIdList);
        ;

        if (ticketList.size() != ticketIdList.size()) {
            // Now you can check if there are any missing IDs
            List<Integer> foundTicketIds = ticketList.stream()
                    .map(Ticket::getTicketId)
                    .toList();

            List<Integer> missingTicketIds = ticketIdList.stream()
                    .filter(id -> !foundTicketIds.contains(id))
                    .toList();

            throw new IllegalArgumentException(String.format("Error deleting tickets, invalid ticket ids: %s", missingTicketIds.toString()));
        } else {
            ticketRepository.deleteAllInBatch(ticketList);
        }
    }

    @Override
    public Integer getMaxConsecutiveSeatsForSection(Integer eventId, String sectionId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new NonExistentException("Event does not exist");
        }
        Venue venue = event.getVenue();
        VenueSectionId venueSectionId = new VenueSectionId(venue, sectionId);
        Section section = sectionRepository.findById(venueSectionId).orElse(null);

        if (section == null) {
            throw new NonExistentException("Section does not exist");
        }

        int maxConsecutiveSeatsForSection = 0;

//getting dimensions of section
        Integer rowNo = section.getNoOfRows();
        Integer colNo = section.getNoOfSeatsPerRow();


        for (int i = 0; i < rowNo; i++) {
            if (ticketRepository.countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), rowNo + 1) == colNo) {
                System.out.println(String.format("ROW %d FULL", i));
                continue;
            }

            //find all tickets for row
            List<Ticket> ticketsForRow = ticketRepository.findAllByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(event.getEventId(), section.getSectionId(), i + 1);

            //initialise all seats for row
            List<Integer> seatsAvailableForRow = new ArrayList<>();
            for (int j = 0; j < colNo; j++) {
                seatsAvailableForRow.add(j + 1);
            }
            //remove occupied seats from seats available list
            ticketsForRow.forEach(ticket -> seatsAvailableForRow.remove(ticket.getSeatNo()));
            int[] availableSeatsArrayForRow = seatsAvailableForRow.stream().mapToInt(Integer::intValue).toArray(); //for the row

            //find consecutive seats
            List<List<Integer>> consecutiveGroupsOfSeats = findConsecutiveGroups(availableSeatsArrayForRow);
            for (List<Integer> group : consecutiveGroupsOfSeats) {
                if (group.size() > maxConsecutiveSeatsForSection) {
                    maxConsecutiveSeatsForSection = group.size();
                }
            }

        }

        return maxConsecutiveSeatsForSection;
    }

    @Override
    public Boolean getEventHasTickets(Event event) {
        int totalSeats = venueRepository.findNoOfSeatsByVenue(event.getVenue().getVenueId());
        int sold = ticketRepository.countTicketsByOrder_Event(event);

        return sold < totalSeats;
    }

    @Override
    public Integer getNumberOfTicketsPurchaseable(Event event, User user) {
        return PresaleService.MAX_TICKETS_SOLD_PER_USER - ticketRepository.countAllByOrder_EventAndOrder_User(event, user);
    }
}
