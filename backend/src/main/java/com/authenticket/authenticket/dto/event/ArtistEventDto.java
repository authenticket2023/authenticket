package com.authenticket.authenticket.dto.event;

public record ArtistEventDto(
        Object artistId,
        Object artistName,
        Object artistImage,
        Object eventId,
        Object organiserId,
        Object venueId,
        Object eventName,
        Object eventDescription,
        Object categoryId,
        Object eventDate,
        Object eventLocation,
        Object otherEventInfo,
        Object totalTickets,
        Object totalTicketsSold,
        Object eventImage,
        Object ticketSaleDate,
        Object typeId
) {
}
