package com.authenticket.authenticket.dto.section;


public record SectionDisplayDto(
        String sectionId,
        Integer venueId,
        Integer catId,
        Integer rowNo,
        Integer seatNo
        ) {
}