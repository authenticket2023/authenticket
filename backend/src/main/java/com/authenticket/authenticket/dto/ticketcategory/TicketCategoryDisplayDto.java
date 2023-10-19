package com.authenticket.authenticket.dto.ticketcategory;


/**
 * Represents a DTO for displaying ticket categories.
 */
public record TicketCategoryDisplayDto(
        /**
         * The unique identifier for the ticket category.
         */
        Integer categoryId,

        /**
         * The name of the ticket category.
         */
        String categoryName) {
}