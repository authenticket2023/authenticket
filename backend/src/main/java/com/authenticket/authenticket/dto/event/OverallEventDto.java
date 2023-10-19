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

/**
 * A DTO representing an overall event.
 */
public record OverallEventDto(
        /**
         * The unique identifier of the event.
         */
        Integer eventId,

        /**
         * The name of the event.
         */
        String eventName,

        /**
         * A description of the event.
         */
        String eventDescription,

        /**
         * The date and time of the event.
         */
        LocalDateTime eventDate,

        /**
         * Additional information about the event.
         */
        String otherEventInfo,

        /**
         * The image associated with the event.
         */
        String eventImage,

        /**
         * The total number of available tickets for the event.
         */
        Integer totalTickets,

        /**
         * The total number of tickets that have been sold for the event.
         */
        Integer totalTicketsSold,

        /**
         * The date and time when ticket sales for the event began.
         */
        LocalDateTime ticketSaleDate,

        /**
         * The review status of the event.
         */
        String reviewStatus,

        /**
         * Remarks from the reviewer regarding the event.
         */
        String reviewRemarks,

        /**
         * The administrator who reviewed the event.
         */
        AdminDisplayDto reviewedBy,

        /**
         * Indicates whether the event is enhanced.
         */
        Boolean isEnhanced,

        /**
         * Indicates whether the event has a presale.
         */
        Boolean hasPresale,

        /**
         * Indicates whether the event is available for presale to specific users.
         */
        Boolean hasPresaleUsers,

        /**
         * The set of ticket categories associated with the event.
         */
        Set<EventTicketCategoryDisplayDto> ticketCategory,

        /**
         * The display information of the event organizer.
         */
        EventOrganiserDisplayDto organiser,

        /**
         * The venue where the event will take place.
         */
        Venue venue,

        /**
         * The set of artists or performers associated with the event.
         */
        Set<ArtistDisplayDto> artists,

        /**
         * The type of the event.
         */
        String type
) {
}