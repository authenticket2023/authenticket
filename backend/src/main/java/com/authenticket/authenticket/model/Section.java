package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


/**
 * Represents a section within a venue, linking a venue to a ticket category and specifying details about the section.
 */
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
    /**
     * The unique identifier for the section.
     */
    @Id
    @Column(name = "section_id")
    private String sectionId;

    /**
     * The venue associated with this section.
     */
    @Id
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    /**
     * The ticket category linked to this section.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private TicketCategory ticketCategory;

    /**
     * The number of rows in this section.
     */
    @Column(name = "no_of_rows", nullable = false)
    private Integer noOfRows;

    /**
     * The number of seats per row in this section.
     */
    @Column(name = "no_of_seats_per_row", nullable = false)
    private Integer noOfSeatsPerRow;
}

