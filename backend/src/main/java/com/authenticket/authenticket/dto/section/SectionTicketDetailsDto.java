package com.authenticket.authenticket.dto.section;


public record SectionTicketDetailsDto(
        Integer sectionId,
        Integer catId,
        Integer totalSeats,
        Integer occupiedSeats,
        Integer availableSeats,
        String status
        ) {
}