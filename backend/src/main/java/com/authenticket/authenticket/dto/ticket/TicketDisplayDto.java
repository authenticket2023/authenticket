package com.authenticket.authenticket.dto.ticket;

public record TicketDisplayDto(Integer ticketId,
                               Integer userId,
                               Integer eventId,
                               Integer categoryId) {
}
