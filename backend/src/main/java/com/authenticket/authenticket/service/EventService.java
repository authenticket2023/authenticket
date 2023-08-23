package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.EventModel;

import java.util.*;

public interface EventService {

    List<EventModel> findAllEvent();
    Optional<EventModel> findById(Long event_id);
    EventModel saveEvent (EventModel eventModel);
    EventModel updateEvent (EventModel eventModel);
    String deleteEvent (Long event_id);
}
