package com.authenticket.authenticket.dto.ticketcategory;

/**
 * Represents the unique identifier for the ticket category.
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
