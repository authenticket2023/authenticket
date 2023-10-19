package com.authenticket.authenticket.dto.admin;

import java.time.LocalDateTime;

/**
 * A DTO representing an admin user for display purposes.
 */
public record AdminDisplayDto(
        /**
         * The unique identifier for the admin user.
         */
        Integer adminId,

        /**
         * The name of the admin user.
         */
        String name,

        /**
         * The email address of the admin user.
         */
        String email,

        /**
         * The role or position of the admin user.
         */
        String role,

        /**
         * The date and time when the admin user was created.
         */
        LocalDateTime createdAt,

        /**
         * The date and time when the admin user was last updated.
         */
        LocalDateTime updatedAt,

        /**
         * The date and time when the admin user was deleted (if applicable).
         */
        LocalDateTime deletedAt
) {
}
