package com.authenticket.authenticket.dto.eventticketcategory;

public record EventTicketCategoryDisplayDto(Integer categoryId,
                                            String categoryName,
                                            Double price,
                                            Integer availableTickets,
                                            Integer totalTicketsPerCat) {
}
