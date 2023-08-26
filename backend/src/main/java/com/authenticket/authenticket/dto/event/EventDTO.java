package com.authenticket.authenticket.DTO.event;

import java.time.LocalDateTime;

//currently only hides the deleted at field
public record EventDTO(Long eventId,
                       String eventName,
                       String eventDescription,
                       LocalDateTime eventDate,
                       String eventLocation,
                       String otherEventInfo,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt
//                       LocalDateTime deletedAt
) {
}
