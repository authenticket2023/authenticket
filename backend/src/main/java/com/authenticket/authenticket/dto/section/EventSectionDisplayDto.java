package com.authenticket.authenticket.dto.section;


public record EventSectionDisplayDto(
        Integer sectionId,
        Integer venueId,
        Integer catId,
        Integer availableTickets,
        Integer totalTickets,
        String status
        ) {
}