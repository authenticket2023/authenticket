package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

public record EventUpdateDto(Integer eventId,
                             String eventName,
                             String eventDescription,
                             LocalDateTime eventDate,
                             String eventLocation,
                             String otherEventInfo,
                             LocalDateTime ticketSaleDate
) {
}
