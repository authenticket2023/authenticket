package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.model.EventOrganiser;
import org.springframework.stereotype.Service;

@Service
public class EventOrganiserUpdateDtoMapper {
    public void apply(EventOrganiserUpdateDto dto, EventOrganiser organiser) {
        if (dto.description() != null) {
            organiser.setDescription(dto.description());
        }
    }
}

