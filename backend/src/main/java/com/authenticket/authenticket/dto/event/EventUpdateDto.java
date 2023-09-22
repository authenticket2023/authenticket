package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.model.Admin;

import java.time.LocalDateTime;

public record EventUpdateDto(Integer eventId,
                             String eventName,
                             String eventDescription,
                             LocalDateTime eventDate,
                             String eventLocation,
                             String otherEventInfo,
                             LocalDateTime ticketSaleDate,
                             Venue venue,
                             EventType eventType,
                             String reviewRemarks,
                             String reviewStatus,
                             Admin reviewedBy
) {
}
