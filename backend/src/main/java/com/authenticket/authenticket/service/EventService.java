package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.FeaturedEvent;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface EventService {

    //get all events for homepage
    List<EventHomeDto> findAllPublicEvent(Pageable pageable);

    //get all events for admin
    List<EventAdminDisplayDto> findAllEvent();
    OverallEventDto findEventById(Integer eventId);

    //get methods
    List<EventHomeDto> findRecentlyAddedEvents(Pageable pageable);
    List<FeaturedEventDto> findFeaturedEvents(Pageable pageable);
    List<EventHomeDto> findBestSellerEvents(Pageable pageable);
    List<EventHomeDto> findUpcomingEventsByTicketSalesDate(Pageable pageable); //based on ticket sale dates
    List<EventHomeDto> findCurrentEventsByEventDate(Pageable pageable); //event date not past the current date
    List<EventHomeDto> findPastEventsByEventDate(Pageable pageable);//event date past the current date


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
    EventDisplayDto addTicketCategory(Integer catId, Integer eventId, Double price);

    void updateTicketPricing(Integer catId, Integer eventId, Double price);

    EventDisplayDto removeTicketCategory(Integer catId, Integer eventId);

    List<SectionTicketDetailsDto> findSectionDetailsForEvent(Event event);

}