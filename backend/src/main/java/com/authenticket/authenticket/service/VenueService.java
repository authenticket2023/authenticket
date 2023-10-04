package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.model.Venue;

import java.util.List;
import java.util.Optional;

public interface VenueService {
    Optional<Venue> findById(Integer venueId);
    List<Venue> findAllVenue();
    Venue saveVenue(Venue venue);
    Venue updateVenue(Integer venueId, String venueName, String venueLocation);
    void removeVenue(Integer venueId);
}
