package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Represents the pricing information for a specific ticket category in the context of an event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_pricing")
@IdClass(EventTicketCategoryId.class)
public class TicketPricing {
    /**
     * The ticket category associated with this pricing information.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private TicketCategory cat;

    /**
     * The event associated with this pricing information.
     */
    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    /**
     * The price of the ticket category for the event.
     */
    @Column(name = "price")
    private Double price;

    /**
     * Returns a string representation of the ticket pricing, including the category, event ID, and price.
     *
     * @return A string representing the ticket pricing information.
     */
    @Override
    public String toString() {
        return "Ticket Pricing: { " + cat.toString() + ", EventID: " + event.getEventId() + ", Price: " + price + " }";
    }

    /**
     * Computes the hash code for the ticket pricing based on the associated event and ticket category.
     *
     * @return The hash code for the ticket pricing.
     */
    @Override
    public int hashCode() {
        return Objects.hash(event, cat);
    }
}
