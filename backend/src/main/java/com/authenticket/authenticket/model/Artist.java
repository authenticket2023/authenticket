package com.authenticket.authenticket.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artist", schema = "dev")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = { "createdAt", "deletedAt", "updatedAt" })
public class Artist extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Integer artistId;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "artist_image", nullable = true)
    private String artistImage;

    @ManyToMany(mappedBy ="artists", cascade = CascadeType.ALL)
    @JsonIgnore
//    @JoinTable(
//            name = "artist_event",
//            joinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "artist_id"),
//            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"))
    private Set<Event> events;
}

