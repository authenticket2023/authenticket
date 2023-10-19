package com.authenticket.authenticket.dto.section;

/**
 * A DTO for Section ticket details.
 */
public record SectionTicketDetailsDto(
        /**
         * The unique identifier of the section.
         */
        String sectionId,

        /**
         * The category identifier associated with the section.
         */
        Integer catId,

        /**
         * The total number of seats in the section.
         */
        Integer totalSeats,

        /**
         * The number of occupied seats in the section.
         */
        Integer occupiedSeats,

        /**
         * The number of available seats in the section.
         */
        Integer availableSeats,

        /**
         * The maximum consecutive available seats in the section.
         */
        Integer maxConsecutiveSeats,

        /**
         * The status of the section.
         */
        String status,

        /**
         * The price of tickets in the section.
         */
        Double ticketPrice
        ) {
}