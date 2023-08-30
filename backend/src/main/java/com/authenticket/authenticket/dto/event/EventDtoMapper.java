package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.Event;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class EventDtoMapper implements Function<Event, EventDto> {
    public EventDto apply(Event event) {
        return new EventDto(
                event.getEventId(), event.getEventName(), event.getEventDescription(),
                event.getEventDate(), event.getEventLocation(),event.getOtherEventInfo(), event.getCreatedAt(), event.getUpdatedAt());
    }
}
