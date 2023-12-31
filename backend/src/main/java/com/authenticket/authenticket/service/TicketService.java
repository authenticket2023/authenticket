package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;

import java.util.List;

public interface TicketService {
    List<TicketDisplayDto> findAllTicket();
    Ticket saveTicket(Ticket ticket);
    TicketDisplayDto findTicketById(Integer ticketId);
    List<TicketDisplayDto> findAllByOrderId(Integer orderId);
    void removeAllTickets(List<Integer> ticketList);
    //seatAllocation
    List<Ticket> allocateSeats( Integer eventId,String sectionId, Integer ticketsToPurchase);
    String[] getSeatCombinationRank(Integer ticketCount);
    Integer getMaxConsecutiveSeatsForSection(Integer eventId, String sectionId);
    Boolean getEventHasTickets(Event event);
    Integer getNumberOfTicketsPurchaseable(Event event, User user);
    void setCheckIn(Integer ticketId, Boolean checkedIn);
}
