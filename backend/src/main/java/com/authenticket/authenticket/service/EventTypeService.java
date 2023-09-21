package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.EventType;

import java.util.List;
import java.util.Optional;

public interface EventTypeService {

    List<EventType> findAllEventType();
//    Optional<EventType> findEventTypeById(Integer eventTypeId);
    EventType saveEventType (EventType eventType);
//    EventType updateEventType (EventType eventType);
//
//    //updates deleted_at field with datetime, DOES NOT really remove the eventType
//    void deleteEventType (Integer eventTypeId);
//    //actually removes the eventType
//    String removeEventType (Integer eventTypeId);
//
//    EventType approveEventType (Integer eventTypeId, Integer adminId);
//
////    EventType rejectEventType (Integer eventTypeId);
}