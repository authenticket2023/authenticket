package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventOrganiserDisplayDtoMapper implements Function<EventOrganiser, EventOrganiserDisplayDto> {
    public EventOrganiserDisplayDto apply(EventOrganiser organiser) {
        return new EventOrganiserDisplayDto(
                organiser.getOrganiserId(), organiser.getName(), organiser.getEmail(),
                organiser.getDescription(), organiser.getVerifiedBy(), organiser.getLogoImage(),
                organiser.getCreatedAt(), organiser.getUpdatedAt());
    }
}

