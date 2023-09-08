package com.authenticket.authenticket.dto.eventticketcategory;

public record EventTicketCategoryUpdateDto(Integer categoryId,
                                           Integer eventId,
                                           Double price,
                                           Integer availableTickets,
                                           Integer totalTicketsPerCat) {
}
