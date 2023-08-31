package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

public record EventDisplayDto(Integer eventId,
                              String eventName,
                              String eventDescription,
                              LocalDateTime eventDate,
                              String eventLocation,
                              String otherEventInfo,
                              LocalDateTime ticketSaleDate,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt
//                       LocalDateTime deletedAt
) {
}
