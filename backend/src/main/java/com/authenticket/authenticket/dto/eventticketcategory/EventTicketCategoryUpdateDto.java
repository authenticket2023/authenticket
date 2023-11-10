package com.authenticket.authenticket.dto.eventticketcategory;

/**
 * A DTO for updating Event Ticket Category information.
 */
public record EventTicketCategoryUpdateDto(
        /**
         * The unique identifier of the category to update.
         */
        Integer categoryId,

        /**
         * The unique identifier of the event associated with this category.
         */
        Integer eventId,

        /**
         * The updated price for the category.
         */
        Double price
) {
}

