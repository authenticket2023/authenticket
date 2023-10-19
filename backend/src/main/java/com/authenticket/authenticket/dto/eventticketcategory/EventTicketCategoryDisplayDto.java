package com.authenticket.authenticket.dto.eventticketcategory;

/**
 * Represents a DTO (Data Transfer Object) for displaying event ticket category information.
 */
public record EventTicketCategoryDisplayDto(
        /**
         * The unique identifier for the ticket category.
         */
        Integer categoryId,

        /**
         * The name or description of the ticket category.
         */
        String categoryName,

        /**
         * The price associated with the ticket category.
         */
        Double price
) {
}