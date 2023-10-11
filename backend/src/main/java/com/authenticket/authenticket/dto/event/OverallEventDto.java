package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.model.*;

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
        String reviewStatus,
        String reviewRemarks,
        AdminDisplayDto reviewedBy,
        Boolean isEnhanced,
        Boolean hasPresale,
        Boolean hasPresaleUsers,
        Set<EventTicketCategoryDisplayDto> ticketCategory,//object
        EventOrganiserDisplayDto organiser, //object
        Venue venue, //object
        Set<ArtistDisplayDto> artists, //object
        String type //object
) {
}
