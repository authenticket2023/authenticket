package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventHomeDto;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.event.OverallEventDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.EventRepository;

import java.util.*;

public interface EventService {

    List<EventDisplayDto> findAllEvent();
    OverallEventDto findEventById(Integer eventId);

    List<EventHomeDto> findRecentlyAddedEvents();
    List<EventHomeDto> findFeaturedEvents();
    List<EventHomeDto> findBestSellerEvents();
    List<EventHomeDto> findUpcomingEvents();

    Event saveEvent (Event event);
    Event updateEvent (EventUpdateDto eventUpdateDto);

    //updates deleted_at field with datetime, DOES NOT really remove the event
    void deleteEvent (Integer eventId);
    //actually removes the event
    String removeEvent (Integer eventId);

    Event approveEvent (Integer eventId, Integer adminId);

//    Event rejectEvent (Integer eventId);

    Set<ArtistDisplayDto> findArtistForEvent(Integer eventId);
}