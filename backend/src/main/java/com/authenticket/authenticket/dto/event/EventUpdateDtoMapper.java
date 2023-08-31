package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.Event;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventUpdateDtoMapper{
    public void apply(EventUpdateDto dto, Event event) {
        if (dto.eventName() != null) {
            event.setEventName(dto.eventName());
        }
        if (dto.eventDescription() != null) {
            event.setEventDescription(dto.eventDescription());
        }
        if (dto.eventDate() != null) {
            event.setEventDate(dto.eventDate());
        }
        if (dto.eventLocation() != null) {
            event.setEventLocation(dto.eventLocation());
        }
        if (dto.otherEventInfo() != null) {
            event.setOtherEventInfo(dto.otherEventInfo());
        }
        if (dto.ticketSaleDate() != null) {
            event.setTicketSaleDate(dto.ticketSaleDate());
        }
    }
}
