package com.authenticket.authenticket.dto.ticketcategory;

public record TicketCategoryUpdateDto(Integer categoryId,
                                      Integer eventId,
                                      String categoryName,
                                      Double price,
                                      Integer availableTickets) {
}
