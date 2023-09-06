package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VenueServiceImpl {
    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VenueDtoMapper venueDtoMapper;

    public Optional<VenueDisplayDto> findById(Integer venueId) {
        return venueRepository.findById(venueId).map(venueDtoMapper);
    }

    public VenueDisplayDto updateVenue(Venue venue){
        Optional<Venue> venueOptional = venueRepository.findById(venue.getVenueId());

        if(venueOptional.isPresent()) {
            Venue existingVenue = venueOptional.get();
            venueDtoMapper.update(venue, existingVenue);
            venueRepository.save(existingVenue);
            return venueDtoMapper.apply(existingVenue);
        }

        return null;
    }

    public String removeVenue(Integer venueId){
        Optional<Venue> venueOptional = venueRepository.findById(venueId);

        if (venueOptional.isPresent()) {
            Venue venue = venueOptional.get();
            if(venue.getDeletedAt()!=null){
                return "venue already deleted";
            }

            venue.setDeletedAt(LocalDateTime.now());
            venueRepository.save(venue);
            return "venue deleted successfully";
        }

        return "error: venue deleted unsuccessfully, venue might not exist";
    }

    public VenueDisplayDto updateVenueImage(String filename, Integer venueId){
        Optional<Venue> venueOptional = venueRepository.findById(venueId);

        if(venueOptional.isPresent()){
            Venue existingVenue = venueOptional.get();
            existingVenue.setVenueImage(filename);
            venueRepository.save(existingVenue);
            return venueDtoMapper.apply(existingVenue);
        }
        return null;
    }
}