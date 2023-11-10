package com.authenticket.authenticket.model;

import lombok.*;

import java.io.Serializable;

/**
 * Represents the composite primary key for the relationship between a venue and a section within the venue.
 * It consists of the venue and section identifiers.
 */
@NoArgsConstructor
@AllArgsConstructor
public class VenueSectionId implements Serializable {
    /**
     * The venue associated with the section.
     */
    private Venue venue;

    /**
     * The identifier of the section within the venue.
     */
    private String sectionId;

    /**
     * Returns a string representation of the composite key.
     *
     * @return A string representing the composite key, including venue and section identifiers.
     */
    @Override
    public String toString(){
        return "{ Venue ID: " + venue.getVenueId() + ", Section Id: " + sectionId + " }";
    }
}
