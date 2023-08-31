package com.authenticket.authenticket.service.impl;


import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDtoMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
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
    private EventOrganiserDisplayDtoMapper eventOrganiserDisplayDtoMapper;

    @Autowired
    private EventOrganiserUpdateDtoMapper eventOrganiserUpdateDtoMapper;

    @Autowired
    private AmazonS3Service amazonS3Service;

    public List<EventOrganiserDisplayDto> findAllEventOrganisers() {
        return eventOrganiserRepository.findAll()
                .stream()
                .map(eventOrganiserDisplayDtoMapper)
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
        return eventOrganiserRepository.findById(organiserId).map(eventOrganiserDisplayDtoMapper);
    }

    public EventOrganiser saveEventOrganiser(EventOrganiser eventOrganiser) {
        return eventOrganiserRepository.save(eventOrganiser);
    }

    public EventOrganiser updateEventOrganiser(EventOrganiserUpdateDto eventOrganiserUpdateDto) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(eventOrganiserUpdateDto.organiserId());

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser existingEventOrganiser = eventOrganiserOptional.get();
            eventOrganiserUpdateDtoMapper.apply(eventOrganiserUpdateDto, existingEventOrganiser);
            eventOrganiserRepository.save(existingEventOrganiser);
            return existingEventOrganiser;
        }

        return null;
    }


    public String deleteEventOrganiser(Integer organiserId) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            if (eventOrganiser.getDeletedAt() != null) {
                return "event organiser already deleted";
            }

            eventOrganiser.setDeletedAt(LocalDateTime.now());
            eventOrganiserRepository.save(eventOrganiser);
            return "event organiser deleted successfully";
        }

        return "error: event organiser deleted unsuccessfully";
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

    public EventOrganiser verifyOrganiser(Integer organiserId, Integer adminId) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser event = eventOrganiserOptional.get();
            event.setVerifiedBy(adminId);
            eventOrganiserRepository.save(event);
            return event;
        }
        return null;
    }
}
