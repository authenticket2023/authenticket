package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;

import java.util.*;

public interface EventService {

    List<Event> findAllEvent();
    Optional<Event> findById(Integer eventId);
    Event saveEvent (Event event);
    Event updateEvent (Event event);

    //updates deleted_at field with datetime, DOES NOT really remove the event
    String deleteEvent (Integer eventId);
    //actually removes the event
    String removeEvent (Integer eventId);

    Event approveEvent (Integer eventId, Integer adminId);

    Event rejectEvent (Integer eventId);
}