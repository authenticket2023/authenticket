package com.authenticket.authenticket.dto.section;

/**
 * A DTO representing a section of a venue, typically used for seating arrangements.
 */
public record SectionDisplayDto(
        /**
         * The unique identifier of the section.
         */
        String sectionId,

        /**
         * The identifier of the venue to which this section belongs.
         */
        Integer venueId,

        /**
         * The category identifier for the section.
         */
        Integer catId,

        /**
         *  The row number within the section.
         */
        Integer rowNo,

        /**
         * The seat number within the row.
         */
        Integer seatNo
) {
}
