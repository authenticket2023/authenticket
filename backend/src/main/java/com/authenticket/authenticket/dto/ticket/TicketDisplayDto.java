package com.authenticket.authenticket.dto.ticket;

public record TicketDisplayDto(Integer ticketId,
                               Long userId,
                               Integer eventId,
                               Integer categoryId) {
}
