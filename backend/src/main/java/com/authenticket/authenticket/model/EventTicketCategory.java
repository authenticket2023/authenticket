package com.authenticket.authenticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_ticket_category", schema = "dev")
@IdClass(EventTicketCategoryId.class)
public class EventTicketCategory {
    @Id
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private TicketCategory cat_id;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event_id;

    @Column(name = "price")
    private Double price;

    @Column(name = "available_tickets")
    private Integer availableTickets;

    @Column(name = "total_tickets_per_cat")
    private Integer totalTicketsPerCat;
}