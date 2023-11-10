package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a venue where events can take place, with attributes such as venue name, location, description, and more.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties(value = {"createdAt", "deletedAt", "updatedAt"})
@Table(name = "venue")
@EqualsAndHashCode(callSuper = true)
public class Venue extends BaseEntity {
    /**
     * The unique identifier for the venue.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Integer venueId;

    /**
     * The name of the venue.
     */
    @Column(name = "venue_name", nullable = false)
    private String venueName;

    /**
     * The location of the venue.
     */
    @Column(name = "venue_location")
    private String venueLocation;

    /**
     * A description of the venue.
     */
    @Column(name = "venue_description", length = 3000)
    private String venueDescription;

    /**
     * The image associated with the venue.
     */
    @Column(name = "venue_image")
    private String venueImage;

    /**
     * A list of sections within the venue.
     */
    @OneToMany(mappedBy = "venue")
    @JsonIgnore
    private List<Section> sections = new ArrayList<>();

    /**
     * Returns the name of the venue.
     *
     * @return The name of the venue.
     */
    @Override
    public String toString() {
        return "Venue Name: " + venueName;
    }
}

