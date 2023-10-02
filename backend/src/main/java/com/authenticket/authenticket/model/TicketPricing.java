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
@Table(name = "ticket_pricing")
@IdClass(EventTicketCategoryId.class)
public class TicketPricing {
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

    @Override
    public String toString(){
        return "Ticket Pricing: { " + cat.toString() + ", EventID: " + event.getEventId() + ", Price: " + price + " }";
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, cat);
    }
}
