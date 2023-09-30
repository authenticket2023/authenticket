package com.authenticket.authenticket.dto.section;


public record SectionDisplayDto(
        Integer sectionId,
        Integer venueId,
        Integer catId,
        Integer rowNo,
        Integer seatNo
        ) {
}