package com.authenticket.authenticket.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class VenueSectionId implements Serializable {
    private Venue venue;

    private String sectionId;

    @Override
    public String toString(){
        return "{ Venue ID: " + venue.getVenueId() + ", Section Id: " + sectionId + " }";
    }
}