package com.authenticket.authenticket.dto.ticketcategory;

public record TicketCategoryDisplayDto(Integer categoryId,
                                       Integer eventId,
                                       String categoryName,
                                       Double price,
                                       Integer availableTickets) {
}
