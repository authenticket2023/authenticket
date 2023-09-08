package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.ArtistRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.EventTicketCategoryRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private EventTicketCategoryRepository eventTicketCategoryRepository;

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

    public OverallEventDto findEventById(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isPresent()){
            Event event = eventOptional.get();
            OverallEventDto overallEventDto = eventDTOMapper.applyOverallEventDto(event);
            return overallEventDto;
        }
        return null;
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
            if(artistSet == null){
                artistSet = new HashSet<>();
            }
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

    public EventDisplayDto addTicketCategory(Integer catId, Integer eventId, Double price, Integer availableTickets, Integer totalTicketsPerCat) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<EventTicketCategory> eventTicketCategoryOptional = eventTicketCategoryRepository.findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isPresent()) {
                throw new AlreadyExistsException("Ticket Category already linked to stated event");
            }

            event.addTicketCategory(ticketCategory, price, availableTickets, totalTicketsPerCat);
            eventRepository.save(event);
            return eventDTOMapper.apply(event);
//            Set<EventTicketCategory> eventTicketCategorySet = event.getEventTicketCategorySet();

//            if(eventTicketCategoryOptional.isEmpty()){
//                EventTicketCategory eventTicketCategory = new EventTicketCategory(ticketCategory, event, price, availableTickets, totalTicketsPerCat);
//                eventTicketCategoryRepository.save(eventTicketCategory);
//
//                eventTicketCategorySet.add(new EventTicketCategory(ticketCategory, event, price, availableTickets, totalTicketsPerCat));
//                System.out.println(eventTicketCategorySet.size());
//                event.setEventTicketCategorySet(eventTicketCategorySet);
//
//                eventRepository.save(event);
//                return eventDTOMapper.apply(event);
//            } else {
//                throw new AlreadyExistsException("Ticket Category already linked to stated event");
//            }
        } else {
            if (categoryOptional.isEmpty()){
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    public void updateTicketCategory(Integer catId, Integer eventId, Double price, Integer availableTickets, Integer totalTicketsPerCat) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<EventTicketCategory> eventTicketCategoryOptional = eventTicketCategoryRepository.findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isEmpty()) {
                throw new NonExistentException("Ticket Category " + ticketCategory.getCategoryName() + " is not linked to Event '" + event.getEventName() + "'");
            }
            EventTicketCategory eventTicketCategory = eventTicketCategoryOptional.get();

            if (!event.updateTicketCategory(eventTicketCategory, price, availableTickets, totalTicketsPerCat)) {
                throw new NonExistentException("Event " + event.getEventName() + " is not linked to " + ticketCategory.getCategoryName());
            }
            eventRepository.save(event);
        } else {
            if (categoryOptional.isEmpty()){
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    public EventDisplayDto removeTicketCategory(Integer catId, Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<EventTicketCategory> eventTicketCategoryOptional = eventTicketCategoryRepository.findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isEmpty()) {
                throw new NonExistentException("Ticket Category not linked to stated event");
            }

            event.removeTicketCategory(ticketCategory);
            eventRepository.save(event);
            return eventDTOMapper.apply(event);
        } else {
            if (categoryOptional.isEmpty()){
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    //return artist for a specific event
    public Set<ArtistDisplayDto> findArtistForEvent(Integer eventId) throws NonExistentException{

        if(eventRepository.findById(eventId).isEmpty()){
            throw new NonExistentException("Event does not exist");
        }
        List<Object[]> artistObject= eventRepository.getArtistByEventId(eventId);
        Set<ArtistDisplayDto> artistDisplayDtoList = artistDtoMapper.mapArtistDisplayDto(artistObject);
        return artistDisplayDtoList;
    }


}
