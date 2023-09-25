package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

public record EventHomeDto(Integer eventId,
                           String eventName,
                           String eventDescription,
                           String eventImage,
                           String eventType,
                           LocalDateTime eventDate,
                           Integer totalTickets,
                           String eventVenue

                           ) {
}
