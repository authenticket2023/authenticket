package com.authenticket.authenticket.dto.ticketcategory;

/**
 * A DTO for updating TicketCategory entities.
 */
public record TicketCategoryUpdateDto(
        /**
         * The unique identifier for the category.
         */
        Integer categoryId,

        /**
         * The unique identifier for the category.
         */
        String categoryName) {
}
