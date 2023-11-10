package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO representing event information for admin display purposes.
 */
public record EventAdminDisplayDto(
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
         * The date and time when tickets for the event go on sale.
         */
        LocalDateTime ticketSaleDate,

        /**
         * The email address of the event organizer.
         */
        String organiserEmail,

        /**
         * Remarks or notes related to the event review.
         */
        String reviewRemarks,

        /**
         * The status of the event review.
         */
        String reviewStatus,

        /**
         * The name of the person who reviewed the event.
         */
        String reviewedBy,

        /**
         * The date and time when the event was deleted or marked for deletion.
         */
        LocalDateTime deletedAt
) {
}