package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

/**
 * A data transfer object (DTO) representing a featured event.
 */
public record FeaturedEventDto(
        /**
         * The unique identifier of the featured event.
         */
        Integer featuredId,

        /**
         * The event details of the featured event, including its name, description, and more.
         */
        EventHomeDto event,

        /**
         * The start date and time for featuring this event.
         */
        LocalDateTime startDate,

        /**
         * The end date and time for featuring this event.
         */
        LocalDateTime endDate
) {
}