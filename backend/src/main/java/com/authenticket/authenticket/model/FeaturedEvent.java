package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "featured_event")
@EqualsAndHashCode(callSuper = true)
public class FeaturedEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "featured_id")
    private Integer featuredId;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "admin_id")
    private Admin admin;



}

