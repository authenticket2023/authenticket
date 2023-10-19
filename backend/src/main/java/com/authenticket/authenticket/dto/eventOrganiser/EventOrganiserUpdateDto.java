package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.model.Admin;

/**
 * A data transfer object (DTO) representing the information needed to update an event organizer.
 */
public record EventOrganiserUpdateDto(
        /**
         * The unique identifier of the event organizer.
         */
        Integer organiserId,

        /**
         * The updated name of the event organizer.
         */
        String name,

        /**
         * The updated description or information about the event organizer.
         */
        String description,

        /**
         * The updated password for the event organizer (if changed).
         */
        String password,

        /**
         * Indicates whether the event organizer is enabled or active.
         */
        Boolean enabled,

        /**
         * The updated review status of the event organizer.
         */
        String reviewStatus,

        /**
         * Updated remarks from the review process (if available).
         */
        String reviewRemarks,

        /**
         * The admin user who reviewed and updated the organizer (if reviewed).
         */
        Admin reviewedBy
) {
}
