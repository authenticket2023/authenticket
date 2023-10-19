package com.authenticket.authenticket.dto.venue;

/**
 * A DTO representing updates to venue information.
 */

public record VenueUpdateDto(
    
    /**
    * The unique identifier for the venue.
    */
    Integer venue_id,
                       
    /**
    * The name of the venue.
    */
    String venue_name,

    /**
    * The location of the venue.
    */         
    String venue_location){
}
