package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.FeaturedEvent;
import com.authenticket.authenticket.repository.EventRepository;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface EventService {

    //get all events for homepage
    List<EventHomeDto> findAllPublicEvent(Pageable pageable);

    //get all events for admin
    List<OverallEventDto> findAllEvent();
    OverallEventDto findEventById(Integer eventId);

    //get methods
    List<EventHomeDto> findRecentlyAddedEvents();
    List<FeaturedEventDto> findFeaturedEvents();
    List<EventHomeDto> findBestSellerEvents();
    List<EventHomeDto> findUpcomingEvents();

    Event saveEvent (Event event);
    FeaturedEventDto saveFeaturedEvent (FeaturedEvent featuredEvent);
    Event updateEvent (EventUpdateDto eventUpdateDto);

    //updates deleted_at field with datetime, DOES NOT really remove the event
    String deleteEvent (Integer eventId);
    //actually removes the event
    String removeEvent (Integer eventId);

    Event approveEvent (Integer eventId, Integer adminId);

//    Event rejectEvent (Integer eventId);

    Set<ArtistDisplayDto> findArtistForEvent(Integer eventId);
    EventDisplayDto addArtistToEvent(Integer artistId, Integer eventId);
    EventDisplayDto addTicketCategory(Integer catId, Integer eventId, Double price, Integer availableTickets, Integer totalTicketsPerCat);

    void updateTicketCategory(Integer catId, Integer eventId, Double price, Integer availableTickets, Integer totalTicketsPerCat);

    EventDisplayDto removeTicketCategory(Integer catId, Integer eventId);
}