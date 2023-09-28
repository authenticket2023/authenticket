package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.model.Ticket;

import java.util.List;

public interface TicketService {
    List<TicketDisplayDto> findAllTicket();
    TicketDisplayDto findTicketById(Integer ticketId);
//    Ticket saveTicket(Integer userId, Integer eventId, Integer categoryId) throws ApiRequestException;
//    Ticket updateTicket(Integer ticketId, Integer userId);
    void deleteTicket(Integer ticketId);
    void removeTicket(Integer ticketId);
}
