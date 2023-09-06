package com.authenticket.authenticket.dto.venue;

import com.authenticket.authenticket.model.Venue;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class VenueDtoMapper implements Function<Venue, VenueDisplayDto> {
    public VenueDisplayDto apply(Venue venue){
        return new VenueDisplayDto(
                venue.getVenueName(),
                venue.getVenueLocation()
        );
    }

    public void update (Venue newVenue, Venue oldVenue){
        if(newVenue.getVenueName() != null){
            oldVenue.setVenueName(newVenue.getVenueName());
        }
        if(newVenue.getVenueLocation() != null){
            oldVenue.setVenueLocation(newVenue.getVenueLocation());
        }
        if(newVenue.getVenueImage() != null){
            oldVenue.setVenueImage(newVenue.getVenueImage());
        }
    }
}
