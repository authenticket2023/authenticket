package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;

import java.util.*;

public interface EventService {

    List<Event> findAllEvent();
    Optional<Event> findById(Long event_id);
    Event saveEvent (Event event);
    Event updateEvent (Event event);
    String deleteEvent (Long event_id);
}