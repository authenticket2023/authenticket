package com.authenticket.authenticket.dto.venue;

import com.authenticket.authenticket.model.Venue;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * This class is responsible for mapping between Venue and VenueDisplayDto.
 *  {@link VenueDisplayDto} DTOs and performing updates on venue entities.
 */

@Service
public class VenueDtoMapper implements Function<Venue, VenueDisplayDto> {

    /**
     * Maps a Venue object to a VenueDisplayDto object.
     *
     * @param venue The Venue object to map.
     * @return A VenueDisplayDto object containing venue information.
     */
    public VenueDisplayDto apply(Venue venue){
        return new VenueDisplayDto(
                venue.getVenueId(),
                venue.getVenueName(),
                venue.getVenueLocation(),
                venue.getVenueImage()
        );
    }

    /**
     * Updates the properties of an existing Venue with new values.
     *
     * @param newVenueDto The VenueUpdateDto object containing updated values.
     * @param oldVenue The existing Venue object to be updated.
     */
    public void update (VenueUpdateDto newVenueDto, Venue oldVenue){
        if(newVenueDto.venue_name() != null){
            oldVenue.setVenueName(newVenueDto.venue_name());
        }
        if(newVenueDto.venue_location() != null){
            oldVenue.setVenueLocation(newVenueDto.venue_location());
        }
    }
}
