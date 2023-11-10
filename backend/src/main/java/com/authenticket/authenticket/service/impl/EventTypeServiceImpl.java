package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventTypeServiceImpl implements EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    /**
     * Constructs an instance of EventTypeServiceImpl with the specified
     * EventTypeRepository
     * dependency. This constructor is used for dependency injection to set the
     * repository
     * needed for managing event types.
     *
     * @param eventTypeRepository The EventTypeRepository to be injected.
     */
    @Autowired
    public EventTypeServiceImpl(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    /**
     * Retrieves and returns a list of all available event types.
     *
     * @return A list of EventType objects representing different event types.
     * @see EventType
     */
    @Override
    public List<EventType> findAllEventType() {
        return eventTypeRepository.findAll();
    }

    /**
     * Saves a new or existing event type to the database. If the event type does
     * not
     * exist, it will be created, and if it already exists, it will be updated.
     *
     * @param eventType The EventType object to be saved or updated.
     * @return The saved or updated EventType object.
     * @see EventType
     */
    @Override
    public EventType saveEventType(EventType eventType) {
        return eventTypeRepository.save(eventType);
    }
}
