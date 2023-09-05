package com.authenticket.authenticket.service.impl;


import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EventOrganiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventOrganiserServiceImpl implements EventOrganiserService {

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private EventOrganiserDtoMapper eventOrganiserDtoMapper;
    

    @Autowired
    private AmazonS3Service amazonS3Service;
    
    @Autowired
    private AdminRepository adminRepository;

    public List<EventOrganiserDisplayDto> findAllEventOrganisers() {
        return eventOrganiserRepository.findAll()
                .stream()
                .map(eventOrganiserDtoMapper)
                .collect(Collectors.toList());
    }

    public List<Event> findAllEventsByOrganiser(Integer organiserId){
        EventOrganiser organiser = eventOrganiserRepository.findById(organiserId).orElse(null);

        if (organiser != null) {
            return organiser.getEvents();
        }

        return new ArrayList<>();
    }

    public Optional<EventOrganiserDisplayDto> findOrganiserById(Integer organiserId) {
        return eventOrganiserRepository.findById(organiserId).map(eventOrganiserDtoMapper);
    }

    public EventOrganiser saveEventOrganiser(EventOrganiser eventOrganiser) {
        return eventOrganiserRepository.save(eventOrganiser);
    }

    public EventOrganiser updateEventOrganiser(EventOrganiserUpdateDto eventOrganiserUpdateDto) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(eventOrganiserUpdateDto.organiserId());

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser existingEventOrganiser = eventOrganiserOptional.get();
            eventOrganiserDtoMapper.update(eventOrganiserUpdateDto, existingEventOrganiser);
            eventOrganiserRepository.save(existingEventOrganiser);
            return existingEventOrganiser;
        }

        return null;
    }

    @Override
    public EventOrganiser updateEventOrganiserImage(Integer organiserId,String filename) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            eventOrganiser.setLogoImage(filename);
            eventOrganiserRepository.save(eventOrganiser);
            return eventOrganiser;
        } else{
            return null;
        }

    }

    public void deleteEventOrganiser(Integer organiserId) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            if (eventOrganiser.getDeletedAt() != null) {
                throw new AlreadyDeletedException("Event organiser already deleted");
            }

            eventOrganiser.setDeletedAt(LocalDateTime.now());
            eventOrganiserRepository.save(eventOrganiser);

        } else {
            throw new NonExistentException("Event organiser does not exists");
        }
    }

    public String removeEventOrganiser(Integer organiserId) {

        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            String logoImage = eventOrganiser.getLogoImage();

                eventOrganiserRepository.deleteById(organiserId);
                if (logoImage != null) {
                    amazonS3Service.deleteFile(logoImage, "event_organiser_profile");
                }


            return "event organiser removed successfully";
        }
        return "error: event organiser does not exist";

    }

    public EventOrganiser approveOrganiser(Integer organiserId, Integer adminId) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);
        Optional<Admin> adminOptional = adminRepository.findById(adminId);

        if (eventOrganiserOptional.isPresent() && adminOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            eventOrganiser.setAdmin(adminOptional.get());
            eventOrganiserRepository.save(eventOrganiser);
            return eventOrganiser;
        }
        return null;
    }
}
