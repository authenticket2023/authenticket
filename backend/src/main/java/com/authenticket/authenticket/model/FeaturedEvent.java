package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a featured event, which is a specific event highlighted for promotion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "featured_event")
@EqualsAndHashCode(callSuper = true)
public class FeaturedEvent extends BaseEntity {
    /**
     * The unique identifier for the featured event, generated using an auto-increment strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "featured_id")
    private Integer featuredId;

    /**
     * The event associated with this featured event.
     */
    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * The start date of the featured event.
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * The end date of the featured event.
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * The administrator responsible for managing the featured event.
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "admin_id")
    private Admin admin;

}

