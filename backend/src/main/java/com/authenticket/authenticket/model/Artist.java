package com.authenticket.authenticket.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artist", schema = "dev")
@EqualsAndHashCode(callSuper = true)
public class Artist extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Integer artistId;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "artist_image")
    private String artistImage;

    @ManyToMany
    Set<Event> events;
}

