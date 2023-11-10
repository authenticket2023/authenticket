package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;

import java.time.LocalDateTime;

/**
 * Represents a DTO for displaying event organiser information.
 */
public record EventOrganiserDisplayDto(
        /**
         * The unique identifier of the event organizer.
         */
        Integer organiserId,

        /**
         * The name of the event organizer.
         */
        String name,

        /**
         * The email address of the event organizer.
         */
        String email,

        /**
         * A brief description of the event organizer.
         */
        String description,

        /**
         * The URL or path to the organizer's logo image.
         */
        String logoImage,

        /**
         * The role of the event organizer (if applicable).
         */
        String role,

        /**
         * The status of the organizer's review process.
         */
        String reviewStatus,

        /**
         * Remarks from the review process (if available).
         */
        String reviewRemarks,

        /**
         * The admin user who reviewed the organizer (if reviewed).
         */
        AdminDisplayDto reviewedBy,

        /**
         * Indicates whether the organizer is enabled or active.
         */
        Boolean enabled,

        /**
         * The date and time when the organizer was created.
         */
        LocalDateTime createdAt,

        /**
         * The date and time of the last update to the organizer's information.
         */
        LocalDateTime updatedAt,

        /**
         * The date and time when the organizer was deleted (if deleted).
         */
        LocalDateTime deletedAt

) {
}