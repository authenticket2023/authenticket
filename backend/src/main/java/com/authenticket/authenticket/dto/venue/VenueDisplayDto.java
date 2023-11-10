package com.authenticket.authenticket.dto.venue;

/**
 * A DTO for displaying venue information.
 */

public record VenueDisplayDto(
        /**
         * The unique identifier for the venue.
         */
        Integer venueId, 
        
        /**
         * The name of the venue.
         */
        String venueName,

        /**
         * The location of the venue.
         */
        String venueLocation,

        /**
         * The image associated with the venue.
         */
        String venueImage){
}
