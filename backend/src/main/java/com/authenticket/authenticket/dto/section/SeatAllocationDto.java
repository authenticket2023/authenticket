package com.authenticket.authenticket.dto.section;


public record SeatAllocationDto(
        Integer sectionId,
        Integer venueId,
        Integer catId,
        Integer rowNo,
        Integer seatNo
        ) {
}