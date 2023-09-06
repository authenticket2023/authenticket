package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.Artist;

import java.time.LocalDateTime;
import java.util.Set;

public record EventDisplayDto(Integer eventId,
                              String eventName,
                              String eventDescription,
                              LocalDateTime eventDate,
                              String eventLocation,
                              String otherEventInfo,
                              LocalDateTime ticketSaleDate,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Set<Artist> artistSet
//                       LocalDateTime deletedAt
) {
}
