package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

//currently only hides the deleted at field
public record EventDto(Long eventId,
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
