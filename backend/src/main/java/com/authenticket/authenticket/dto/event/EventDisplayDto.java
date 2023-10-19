package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
import com.authenticket.authenticket.model.Artist;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO representing event information for display purposes.
 */
public record EventDisplayDto(
        /**
         * The unique identifier of the event.
         */
        Integer eventId,

        /**
         * The name of the event.
         */
        String eventName,

        /**
         * The description of the event.
         */
        String eventDescription,

        /**
         * The date and time of the event.
         */
        LocalDateTime eventDate,

        /**
         * Additional information related to the event.
         */
        String otherEventInfo,

        /**
         * The date and time when tickets for the event go on sale.
         */
        LocalDateTime ticketSaleDate,

        /**
         * The date and time when the event was created.
         */
        LocalDateTime createdAt,

        /**
         * The date and time when the event was last updated.
         */
        LocalDateTime updatedAt,

        /**
         * A set of artists associated with the event.
         */
        Set<Artist> artistSet,

        /**
         * A set of event ticket categories associated with the event.
         */
        Set<EventTicketCategoryDisplayDto> ticketCategorySet
) {
}