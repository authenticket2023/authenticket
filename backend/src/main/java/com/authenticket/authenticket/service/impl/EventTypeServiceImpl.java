package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
public class EventTypeServiceImpl implements EventTypeService {
    @Autowired
    private EventTypeRepository eventTypeRepository;

    public EventType saveEventType(EventType eventType){
       return eventTypeRepository.save(eventType);
   }
}
