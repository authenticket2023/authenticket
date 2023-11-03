package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Represents a ticket for an event, specifying details such as the pricing, location, and holder of the ticket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket extends BaseEntity implements Comparable<Ticket> {
    /**
     * The unique identifier for the ticket.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    /**
     * The pricing details associated with this ticket, including the event and ticket category.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false),
            @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    })
    private TicketPricing ticketPricing;

    /**
     * The section and venue information for the ticket's location.
     */
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "venue_id", referencedColumnName = "venue_id", nullable = false),
            @JoinColumn(name = "section_id", referencedColumnName = "section_id", nullable = false)
    })
    private Section section;

    /**
     * The row number of the seat for the ticket.
     */
    @Column(name = "row_no")
    private Integer rowNo;

    /**
     * The seat number for the ticket.
     */
    @Column(name = "seat_no")
    private Integer seatNo;

    /**
     * The name of the ticket holder.
     */
    @Column(name = "ticket_holder")
    private String ticketHolder;

    /**
     * The order associated with this ticket.
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * A flag indicating whether the ticket has been checked in.
     */
    @Column(name = "checked_in")
    private Boolean checkedIn;

    /**
     * Computes the hash code for the ticket based on its unique identifier.
     *
     * @return The hash code for the ticket.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    /**
     * Compares this ticket to another ticket based on their unique identifiers.
     *
     * @param t The other ticket to compare to.
     * @return A negative value if this ticket's ID is smaller, a positive value if larger, and zero if equal to the other ticket's ID.
     */
    @Override
    public int compareTo(Ticket t) {
        return ticketId - t.getTicketId();
    }

    /**
     * Generates a string representation of the ticket, including its ID and holder's name.
     *
     * @return A string representing the ticket.
     */
    @Override
    public String toString() {
        return "Ticket ID: " + ticketId + ", TicketHolder: " + ticketHolder;
    }
}
