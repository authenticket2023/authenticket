package com.authenticket.authenticket.dto.eventticketcategory;

public record EventTicketCategoryDisplayDto(Integer categoryId,
                                            Integer eventId,
                                            Double price,
                                            Integer availableTickets,
                                            Integer totalTicketsPerCat) {
}
