package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl {
    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VenueDtoMapper venueDtoMapper;

    public Optional<VenueDisplayDto> findById(Integer venueId) {
        return venueRepository.findById(venueId).map(venueDtoMapper);
    }

    public List<VenueDisplayDto> findAllVenue() {
        return venueRepository.findAll()
                .stream()
                .map(venueDtoMapper)
                .collect(Collectors.toList());
    }

    public Venue saveVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    public Venue updateVenue(Integer venueId, String venueName, String venueLocation) {
        Optional<Venue> optionalOldVenue = venueRepository.findById(venueId);
        Venue oldVenue = optionalOldVenue.get();
        if (optionalOldVenue.isEmpty()) {
            throw new NonExistentException("Venue with ID " + venueId + " does not exist");
        }

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

    public void removeVenue(Integer venueId){
        Optional<Venue> venueOptional = venueRepository.findById(venueId);

        if (venueOptional.isPresent()) {
            venueRepository.deleteById(venueId);
        } else {
            throw new NonExistentException("Ticket does not exist");
        }
//        Optional<Venue> venueOptional = venueRepository.findById(venueId);
//
//        if (venueOptional.isPresent()) {
//            Venue venue = venueOptional.get();
//            if(venue.getDeletedAt()!=null){
//                throw new AlreadyDeletedException("Venue already deleted");
//            }
//
//            venue.setDeletedAt(LocalDateTime.now());
//            venueRepository.save(venue);
//        } else {
//            throw new NonExistentException("Venue does not exist");
//        }
    }
}