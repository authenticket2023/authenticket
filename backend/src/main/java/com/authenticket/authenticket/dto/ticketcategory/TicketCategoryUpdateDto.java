package com.authenticket.authenticket.dto.ticketcategory;

/**
 * A data transfer object (DTO) for updating TicketCategory entities.
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
