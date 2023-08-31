package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventDisplayDtoMapper;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.event.EventUpdateDtoMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventDisplayDtoMapper eventDisplayDTOMapper;

    @Autowired
    private EventUpdateDtoMapper eventUpdateDtoMapper;

    @Autowired
    private AmazonS3Service amazonS3Service;

    public List<EventDisplayDto> findAllEvent() {
        return eventRepository.findAll()
                .stream()
                .map(eventDisplayDTOMapper)
                .collect(Collectors.toList());
    }

    public Optional<EventDisplayDto> findEventById(Integer eventId) {
        return eventRepository.findById(eventId).map(eventDisplayDTOMapper);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(EventUpdateDto eventUpdateDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventUpdateDto.eventId());

        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();
            eventUpdateDtoMapper.apply(eventUpdateDto, existingEvent);
            eventRepository.save(existingEvent);
            return existingEvent;
        }

        return null;
    }


    public String deleteEvent(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if(event.getDeletedAt()!=null){
                return "event already deleted";
            }

            event.setDeletedAt(LocalDateTime.now());
            eventRepository.save(event);
            return "event deleted successfully";
        }

        return "error: event deleted unsuccessfully";
    }

    public String removeEvent(Integer eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getEventImage() != null) {
                try {
                    amazonS3Service.deleteFile(event.getEventImage(), "event_images");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            eventRepository.deleteById(eventId);
            return "event removed successfully";
        }
        return "error: event does not exist";

    }

    public Event approveEvent(Integer eventId, Integer adminId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setApprovedBy(adminId);
            eventRepository.save(event);
            return event;
        }
        return null;
    }
}
