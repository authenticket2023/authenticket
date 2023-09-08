package com.authenticket.authenticket.dto.venue;

import java.time.LocalDate;

public record VenueDisplayDto(
        Integer venueId, String venueName,
                              String venueLocation,
        String venueImage){
}
