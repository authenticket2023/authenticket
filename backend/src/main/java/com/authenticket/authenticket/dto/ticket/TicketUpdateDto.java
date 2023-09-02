package com.authenticket.authenticket.dto.ticket;

public record TicketUpdateDto(Integer ticketId,
                              Long userId,
                              Integer eventId,
                              Integer categoryId) {
}
