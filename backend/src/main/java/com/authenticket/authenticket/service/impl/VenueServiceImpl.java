package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueUpdateDto;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the VenueService interface for managing and interacting with venue-related operations.
 */

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    private final VenueDtoMapper venueDtoMapper;

    @Autowired
    public VenueServiceImpl(VenueRepository venueRepository, VenueDtoMapper venueDtoMapper) {
        this.venueRepository = venueRepository;
        this.venueDtoMapper = venueDtoMapper;
    }

    /**
    * Retrieve a Venue by its unique identifier.
    *
    * @param venueId The unique identifier of the Venue to retrieve.
    * @return An Optional containing the found Venue if it exists, or an empty Optional if not.
    */
    @Override
    public Optional<Venue> findById(Integer venueId) {
        return venueRepository.findById(venueId);
    }

    /**
     * Retrieve a list of all Venue objects.
     *
     * @return A List of Venue objects representing all available venues.
     */
    @Override
    public List<Venue> findAllVenue() {
        return venueRepository.findAll();
    }

    /**
     * Save a new Venue to the repository.
     *
     * @param venue The Venue object to be saved.
     * @return The saved Venue object.
     */
    @Override
    public Venue saveVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    /**
     * Update the name and location of an existing Venue by its unique identifier.
     *
     * @param venueId       The unique identifier of the Venue to update.
     * @param venueName     The new name for the Venue (can be empty to retain the old name).
     * @param venueLocation The new location for the Venue (can be empty to retain the old location).
     * @return The updated Venue object.
     * @throws NonExistentException   If the specified Venue does not exist.
     * @throws AlreadyExistsException If the new name is already in use by another Venue.
     */
    @Override
    public Venue updateVenue(Integer venueId, String venueName, String venueLocation) {
        Optional<Venue> optionalOldVenue = venueRepository.findById(venueId);
        if (optionalOldVenue.isEmpty()) {
            throw new NonExistentException("Venue with ID " + venueId + " does not exist");
        }
        Venue oldVenue = optionalOldVenue.get();

        Optional<Venue> venueUpdateNameCheck = venueRepository.findByVenueName(venueName);
        if (venueUpdateNameCheck.isPresent() && (optionalOldVenue.get() != venueUpdateNameCheck.get())) {
            throw new AlreadyExistsException("Venue with name '" + venueName + "' already exists");
        }

        // Use old name if new name is empty
        String updatedVenueName = venueName;
        if (venueName == null || venueName.isEmpty()) {
            updatedVenueName = oldVenue.getVenueName();
        }

        //Use old location if new location is empty
        String updatedVenueLocation = venueLocation;
        if (venueLocation == null || venueLocation.isEmpty()) {
            updatedVenueLocation = oldVenue.getVenueLocation();
        }

        VenueUpdateDto venueUpdateDto = new VenueUpdateDto(venueId, updatedVenueName, updatedVenueLocation);
        venueDtoMapper.update(venueUpdateDto, oldVenue);
        return venueRepository.save(oldVenue);
    }

    /**
     * Remove a Venue from the repository by its unique identifier.
     *
     * @param venueId The unique identifier of the Venue to remove.
     * @throws NonExistentException If the specified Venue does not exist.
     */
    @Override
    public void removeVenue(Integer venueId){
        Optional<Venue> venueOptional = venueRepository.findById(venueId);

        if (venueOptional.isPresent()) {
            venueRepository.deleteById(venueId);
        } else {
            throw new NonExistentException("Ticket does not exist");
        }
    }
}