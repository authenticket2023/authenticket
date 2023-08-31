package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;

import java.util.*;

public interface EventService {

    List<Event> findAllEvent();
    Optional<Event> findById(Integer event_id);
    Event saveEvent (Event event);
    Event updateEvent (Event event);

    //updates deleted_at field with datetime, DOES NOT really remove the event
    String deleteEvent (Integer event_id);

    String removeEvent (Integer event_id);
}