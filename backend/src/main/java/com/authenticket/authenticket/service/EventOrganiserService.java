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
    List<Event> findAllCurrentEventsByOrganiser(Integer organiserId);
    List<EventOrganiserDisplayDto> findEventOrganisersByReviewStatus(String status);
    Optional<EventOrganiserDisplayDto> findOrganiserById(Integer organiserId);
    EventOrganiser saveEventOrganiser (EventOrganiser eventOrganiser);
    EventOrganiser updateEventOrganiser (EventOrganiserUpdateDto eventOrganiserUpdateDto);
    EventOrganiser updateEventOrganiserImage (Integer organiserId,String filename);

    //updates deleted_at field with datetime, DOES NOT really remove the event
    String deleteEventOrganiser (Integer organiserId);

//    EventOrganiser approveOrganiser(Integer organiserId, Integer adminId, String status, String remarks);

//    EventOrganiser rejectOrganiser (Integer organiserId);
}
