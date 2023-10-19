package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.model.Admin;

import java.time.LocalDateTime;

/**
 * A data transfer object (DTO) for updating event information.
 */
public record EventUpdateDto(
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
         * The location of the event.
         */
        String eventLocation,

        /**
         * Additional information about the event.
         */
        String otherEventInfo,

        /**
         * The date and time when tickets for the event will go on sale.
         */
        LocalDateTime ticketSaleDate,

        /**
         * The venue where the event is hosted.
         */
        Venue venue,

        /**
         * The type or category of the event.
         */
        EventType eventType,

        /**
         * Remarks from the event review process.
         */
        String reviewRemarks,

        /**
         * The review status of the event.
         */
        String reviewStatus,

        /**
         * The admin user who reviewed the event.
         */
        Admin reviewedBy
) {
}
