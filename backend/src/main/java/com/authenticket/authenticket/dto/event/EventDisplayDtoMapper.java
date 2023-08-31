package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.Event;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class EventDisplayDtoMapper implements Function<Event, EventDisplayDto> {
    public EventDisplayDto apply(Event event) {
        return new EventDisplayDto(
                event.getEventId(), event.getEventName(), event.getEventDescription(),
                event.getEventDate(), event.getEventLocation(),event.getOtherEventInfo(), event.getTicketSaleDate(), event.getCreatedAt(), event.getUpdatedAt());
    }
}
