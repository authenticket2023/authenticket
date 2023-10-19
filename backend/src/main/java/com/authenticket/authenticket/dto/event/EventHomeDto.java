package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

/**
 * A DTO representing event information for display on the home page.
 */
public record EventHomeDto(
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
         * The image associated with the event.
         */
        String eventImage,

        /**
         * The type of the event.
         */
        String eventType,

        /**
         * The date and time of the event.
         */
        LocalDateTime eventDate,

        /**
         * The total number of tickets available for the event.
         */
        Integer totalTickets,

        /**
         * The venue where the event is hosted.
         */
        String eventVenue
) {
}
