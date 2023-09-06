package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventDtoMapper;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.ArtistRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventDtoMapper eventDTOMapper;

    @Autowired
    private ArtistDtoMapper artistDtoMapper;

    @Autowired
    private AmazonS3Service amazonS3Service;

    public List<EventDisplayDto> findAllEvent() {
        return eventRepository.findAll()
                .stream()
                .map(eventDTOMapper)
                .collect(Collectors.toList());
    }

    public Optional<EventDisplayDto> findEventById(Integer eventId) {
        return eventRepository.findById(eventId).map(eventDTOMapper);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(EventUpdateDto eventUpdateDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventUpdateDto.eventId());

        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();
            eventDTOMapper.update(eventUpdateDto, existingEvent);
            eventRepository.save(existingEvent);
            return existingEvent;
        }

        return null;
    }


    public void deleteEvent(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getDeletedAt() != null) {
                throw new AlreadyDeletedException("Event already deleted");
            }

            event.setDeletedAt(LocalDateTime.now());
            eventRepository.save(event);
        } else {
            throw new NonExistentException("Event does not exists");
        }

    }

    public String removeEvent(Integer eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            String imageName = event.getEventImage();
            if (event.getEventImage() != null) {
                amazonS3Service.deleteFile(imageName, "event_organiser_profile");
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

    public EventDisplayDto addArtistToEvent(Integer artistId, Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<Artist> artistOptional = artistRepository.findById(artistId);
        System.out.println("artist: " + artistId);
        System.out.println("event: " +eventId);
        if (artistOptional.isPresent() && eventOptional.isPresent()) {
            System.out.println("hello");
            Artist artist = artistOptional.get();
            Event event = eventOptional.get();
            Set<Artist> artistSet= event.getArtists();
            if(!artistSet.contains(artist)){

                artistSet.add(artist);
                event.setArtists(artistSet);

                eventRepository.save(event);
                return eventDTOMapper.apply(event);
            } else {
                throw new AlreadyExistsException("Artist already linked to stated event");
            }
        } else {
            if (artistOptional.isEmpty()){
                throw new NonExistentException("Artist does not exists");
            } else {
                throw new NonExistentException("Event does not exists");
            }
        }
    }

}
