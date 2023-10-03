package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "section")
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = {"createdAt", "deletedAt", "updatedAt"})
@IdClass(VenueSectionId.class)
public class Section extends BaseEntity {
    @Id
    @Column(name = "section_id")
    private String sectionId;

    @Id
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private TicketCategory ticketCategory;

    @Column(name = "no_of_rows", nullable = false)
    private Integer noOfRows;

    @Column(name = "no_of_seats_per_row", nullable = false)
    private Integer noOfSeatsPerRow;
}

