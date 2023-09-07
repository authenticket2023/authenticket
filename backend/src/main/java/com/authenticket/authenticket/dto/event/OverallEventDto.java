package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.TicketCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record OverallEventDto(
        Integer eventId,
        String eventName,
        String eventDescription,
        LocalDateTime eventDate,
        String otherEventInfo,
        String eventImage,
        Integer totalTickets,
        Integer totalTicketsSold,
        LocalDateTime ticketSaleDate,
        Object ticketCategory,//object
        EventOrganiserDisplayDto organiser, //object
        VenueDisplayDto venue, //object
        Set<ArtistDisplayDto> artistSet, //object
        String type //object
) {
}
