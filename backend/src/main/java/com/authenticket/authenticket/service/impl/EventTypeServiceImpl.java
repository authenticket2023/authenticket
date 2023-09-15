package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventTypeServiceImpl implements EventTypeService {

    private final EventTypeRepository eventTypeRepository;
    @Autowired
    public EventTypeServiceImpl(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    public EventType saveEventType(EventType eventType){
       return eventTypeRepository.save(eventType);
   }
}
