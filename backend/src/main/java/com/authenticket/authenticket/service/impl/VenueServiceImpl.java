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

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    private final VenueDtoMapper venueDtoMapper;

    @Autowired
    public VenueServiceImpl(VenueRepository venueRepository, VenueDtoMapper venueDtoMapper) {
        this.venueRepository = venueRepository;
        this.venueDtoMapper = venueDtoMapper;
    }

    @Override
    public Optional<Venue> findById(Integer venueId) {
        return venueRepository.findById(venueId);
    }

    @Override
    public List<Venue> findAllVenue() {
        return venueRepository.findAll();
    }

    @Override
    public Venue saveVenue(Venue venue) {
        return venueRepository.save(venue);
    }

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