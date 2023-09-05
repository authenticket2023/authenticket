package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;

import java.util.List;
import java.util.Optional;

public interface EventOrganiserService {
    List<EventOrganiserDisplayDto> findAllEventOrganisers();

    List<Event> findAllEventsByOrganiser(Integer organiserId);
    Optional<EventOrganiserDisplayDto> findOrganiserById(Integer organiserId);
    EventOrganiser saveEventOrganiser (EventOrganiser eventOrganiser);
    EventOrganiser updateEventOrganiser (EventOrganiserUpdateDto eventOrganiserUpdateDto);
    EventOrganiser updateEventOrganiserImage (Integer organiserId,String filename);

    //updates deleted_at field with datetime, DOES NOT really remove the event
    String deleteEventOrganiser (Integer organiserId);
    //actually removes the event
    String removeEventOrganiser (Integer organiserId);

    EventOrganiser approveOrganiser(Integer organiserId, Integer adminId);

//    EventOrganiser rejectOrganiser (Integer organiserId);
}
