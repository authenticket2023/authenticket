package com.authenticket.authenticket.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


/**
 * The `Artist` entity represents an artist in the system.
 *
 * This entity is used to store information about artists, including their name and associated events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artist")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = { "createdAt", "deletedAt", "updatedAt" })
public class Artist extends BaseEntity {
    /**
     * The unique identifier for the artist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Integer artistId;

    /**
     * The name of the artist.
     */
    @Column(name = "artist_name", nullable = false)
    private String artistName;

    /**
     * The image associated with the artist.
     */
    @Column(name = "artist_image")
    private String artistImage;

    /**
     * A set of events associated with this artist.
     */
    @ManyToMany(mappedBy ="artists", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Event> events;
}

