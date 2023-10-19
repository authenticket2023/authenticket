package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO representing an order with its details.
 */
public record OrderDisplayDto(
        /**
         * The unique identifier of the order.
         */
        Integer orderId,

        /**
         * The unique identifier of the event.
         */
        Integer eventId,

        /**
         * The name of the event.
         */
        String eventName,

        /**
         * The date of the event.
         */
        LocalDateTime eventDate,

        /**
         * The venue of the event.
         */
        String venueName,

        /**
         * The total order amount.
         */
        Double orderAmount,

        /**
         * The purchase date of the order.
         */
        LocalDate purchaseDate,

        /**
         * The status of the order (e.g., pending, completed, etc.).
         */
        String orderStatus,

        /**
         * The user who made the purchase, represented as a UserDisplayDto.
         */
        UserDisplayDto purchaser,

        /**
         * The set of tickets included in the order, represented as TicketDisplayDto objects.
         */
        Set<TicketDisplayDto> ticketSet
) {
}



