package com.authenticket.authenticket.dto.section;


public record SectionTicketDetailsDto(
        String sectionId,
        Integer catId,
        Integer totalSeats,
        Integer occupiedSeats,
        Integer availableSeats,
        Integer maxConsecutiveSeats,
        String status
        ) {
}