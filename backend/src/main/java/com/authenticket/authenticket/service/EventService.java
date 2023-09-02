package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.event.EventDto;
import com.authenticket.authenticket.dto.event.EventDtoMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.EventRepository;

import java.util.*;

public interface EventService {

    List<EventDto> findAllEvent();
    Optional<EventDto> findById(Long event_id);
    Event saveEvent (Event event);
    Event updateEvent (Event event);
    String deleteEvent (Long event_id);
}