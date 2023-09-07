package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
    private TicketCategory cat;

    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    @Column(name = "price")
    private Double price;

    @Column(name = "available_tickets")
    private Integer availableTickets;

    @Column(name = "total_tickets_per_cat")
    private Integer totalTicketsPerCat;

    @Override
    public int hashCode() {
        return Objects.hash(event, cat);
    }
}
