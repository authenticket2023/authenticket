package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;


import java.util.List;

public interface TicketService {
    List<TicketDisplayDto> findAllTicket();

    TicketDisplayDto findTicketById(Integer ticketId);

    List<TicketDisplayDto> findAllByOrderId(Integer orderId);
//    Ticket saveTicket(Integer userId, Integer eventId, Integer categoryId) throws ApiRequestException;
//    Ticket updateTicket(Integer ticketId, Integer userId);
//    void deleteTicket(Integer ticketId);
//    void removeTicket(Integer ticketId);
    void removeAllTickets(List<Integer> ticketList);

    //seatAllocation
    List<Ticket> allocateSeats(Integer sectionId, Integer eventId, Integer ticketsToPurchase);
    int[][] getCurrentSeatMatrix(Event event, Section section);
    int[][] getNewSeatMatrix(int[][] currentSeatMatrix, List<Ticket> newTicketsList);
    List<Ticket> findConsecutiveSeatsOf(Event event, Section section, Integer ticketCount);
    String[] getSeatCombinationRank(Integer ticketCount);
    List<List<Integer>> findConsecutiveGroups(int[] availableSeatsArrayForRow);
    List<Integer> getRandomSubsetOfSeats(List<List<Integer>> consecutiveGroups, int n);
    Integer getNoOfAvailableSeatsBySectionForEvent(Event event, Section section);
    Integer getMaxConsecutiveSeatsForSection(Integer eventId, Integer sectionId);

}
