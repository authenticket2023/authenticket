package com.authenticket.authenticket.dto.ticket;

/**
 * A DTO for displaying ticket information.
 */
public record TicketDisplayDto(
        /**
         * The unique identifier of the ticket.
         */
        Integer ticketId,

        /**
         * The identifier of the associated event.
         */
        Integer eventId,

        /**
         * The category identifier of the ticket.
         */
        Integer catId,

        /**
         * The unique identifier of the section.
         */
        String sectionId,

        /**
         * The row number of the seat.
         */
        Integer rowNo,

        /**
         * The seat number.
         */
        Integer seatNo,

        /**
         * The name of the ticket holder.
         */
        String ticketHolder,

        /**
         * The identifier of the associated order.
         */
        Integer orderId,

        /**
         * A flag indicating whether the ticket has been checked in.
         */
        Boolean checkedIn
) {
}

