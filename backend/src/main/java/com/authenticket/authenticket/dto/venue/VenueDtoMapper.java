package com.authenticket.authenticket.dto.venue;

import com.authenticket.authenticket.model.Venue;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class VenueDtoMapper implements Function<Venue, VenueDisplayDto> {
    public VenueDisplayDto apply(Venue venue){
        return new VenueDisplayDto(
                venue.getVenueId(),
                venue.getVenueName(),
                venue.getVenueLocation(),
                venue.getVenueImage()
        );
    }

    public void update (VenueUpdateDto newVenueDto, Venue oldVenue){
        if(newVenueDto.venue_name() != null){
            oldVenue.setVenueName(newVenueDto.venue_name());
        }
        if(newVenueDto.venue_location() != null){
            oldVenue.setVenueLocation(newVenueDto.venue_location());
        }
    }
}
