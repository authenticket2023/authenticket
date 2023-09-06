package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventDtoMapper implements Function<Event, EventDisplayDto> {
    public EventDisplayDto apply(Event event) {
        return new EventDisplayDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getEventLocation(),
                event.getOtherEventInfo(),
                event.getTicketSaleDate(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                event.getArtists());
    }

    public void update(EventUpdateDto dto, Event event) {
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
    public ArtistEventDto applyAssignedEvent(Object[] assignedEvents){
        return new ArtistEventDto(
                assignedEvents[0],
                assignedEvents[1],
                assignedEvents[2],
                assignedEvents[3],
                assignedEvents[4],
                assignedEvents[5],
                assignedEvents[6],
                assignedEvents[7],
                assignedEvents[8],
                assignedEvents[9],
                assignedEvents[10],
                assignedEvents[11],
                assignedEvents[12],
                assignedEvents[13],
                assignedEvents[14]
        );
    }

    public List<ArtistEventDto> mapAssignedEvent(List<Object[]> eventObjects) {
        return eventObjects.stream()
                .map(this::applyAssignedEvent)
                .collect(Collectors.toList());
    }
}
