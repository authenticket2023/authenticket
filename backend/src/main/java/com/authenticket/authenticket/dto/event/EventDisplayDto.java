package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.EventTicketCategory;

import java.time.LocalDateTime;
import java.util.Set;

public record EventDisplayDto(Integer eventId,
                              String eventName,
                              String eventDescription,
                              LocalDateTime eventDate,
                              String otherEventInfo,
                              LocalDateTime ticketSaleDate,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Set<Artist> artistSet,
                              Set<EventTicketCategoryDisplayDto> ticketCategorySet
) {
}
