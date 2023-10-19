package com.authenticket.authenticket.dto.artist;

import java.time.LocalDateTime;

/**
 * Represents a Data Transfer Object (DTO) for displaying artist information.
 * This DTO includes essential details about an artist.
 */
public record ArtistDisplayDto(
        /**
         * The unique identifier of the artist.
         */
        Integer artistId,

        /**
         * The name of the artist.
         */
        String artistName,

        /**
         * The image associated with the artist.
         */
        String artistImage,

        /**
         * The timestamp when the artist record was created.
         */
        LocalDateTime createdAt,

        /**
         * The timestamp when the artist record was last updated.
         */
        LocalDateTime updatedAt,

        /**
         * The timestamp when the artist record was marked as deleted (if applicable).
         */
        LocalDateTime deletedAt
) {
}



