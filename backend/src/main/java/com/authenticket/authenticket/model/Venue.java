package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties(value = { "createdAt", "deletedAt", "updatedAt" })
@Table(name = "venue")
@EqualsAndHashCode(callSuper = true)
public class Venue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Integer venueId;

    @Column(name = "venue_name", nullable = false)
    private String venueName;

    @Column(name = "venue_location")
    private String venueLocation;

    @Column(name = "venue_image")
    private String venueImage;

    @OneToMany( mappedBy = "venue")
    @JsonIgnore
    private List<Section> sections = new ArrayList<>();

    @Override
    public String toString(){
        return "Venue Name: " + venueName;
    }
}

