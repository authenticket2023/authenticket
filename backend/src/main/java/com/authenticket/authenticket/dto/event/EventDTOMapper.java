package com.authenticket.authenticket.DTO.event;

import com.authenticket.authenticket.model.Event;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventDTOMapper implements Function<Event, EventDTO> {
    public EventDTO apply(Event event) {
        return new EventDTO(
                event.getEventId(), event.getEventName(), event.getEventDescription(),
                event.getEventDate(), event.getEventLocation(),event.getOtherEventInfo(), event.getCreatedAt(), event.getUpdatedAt());
    }
}
