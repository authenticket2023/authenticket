package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents an order for purchasing tickets to an event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order")
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity{

    /**
     * Enumerates the possible statuses for an order.
     */
    public enum Status {

        /**
         * Represents an order that is in the processing state.
         */
        PROCESSING("Processing"),

        /**
         * Represents a successful order.
         */
        SUCCESS("Success"),

        /**
         * Represents a cancelled order.
         */
        CANCELLED("Cancelled");

        /**
         * The value associated with each status.
         */
        private final String statusValue;

        /**
         * Constructs a status with the given value.
         *
         * @param statusValue The value associated with the status.
         */
        Status(String statusValue) {
            this.statusValue = statusValue;
        }

        /**
         * Get the value associated with the status.
         *
         * @return The status value.
         */
        public String getStatusValue() {
            return statusValue;
        }
    }



    /**
     * The unique identifier for the order, generated using an auto-increment strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * The total amount of the order.
     */
    @Column(name = "order_amount", nullable = false)
    private Double orderAmount;

    /**
     * The date of purchase for the order.
     */
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /**
     * The status of the order, which can be one of the defined order statuses (Processing, Success, or Cancelled).
     */
    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    /**
     * The user who placed the order.
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The event for which the tickets are being ordered.
     */
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * The set of tickets included in this order.
     */
    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private Set<Ticket> ticketSet = new HashSet<>();

    /**
     * Get the set of tickets included in this order.
     *
     * @return The set of tickets.
     */
    @JsonIgnore
    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    /**
     * Add a ticket to the order's set of tickets.
     *
     * @param ticket The ticket to be added.
     */
    public void addTicket(Ticket ticket) {
        ticketSet.add(ticket);
    }

    /**
     * Update the ticket holder information for a specific ticket in the order.
     *
     * @param ticket       The ticket to be updated.
     * @param ticketHolder The new ticket holder information.
     * @return True if the update is successful, false otherwise.
     */
    public boolean updateTicket(Ticket ticket, String ticketHolder) {
        for (Ticket ticketIter : ticketSet) {
            if (ticketIter.equals(ticket)) {
                ticketIter.setTicketHolder(ticketHolder);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a ticket from the order by its unique identifier.
     *
     * @param ticketId The unique identifier of the ticket to be removed.
     */
    public void removeTicket(Integer ticketId) {
        for (Iterator<Ticket> iterator = ticketSet.iterator(); iterator.hasNext(); ) {
            Ticket ticket = iterator.next();

            if (ticket.getOrder().equals(this) && ticket.getTicketId().equals(ticketId)) {
                iterator.remove();
            }
        }
    }

    /**
     * Generate a hash code for the order based on its unique identifier.
     *
     * @return The hash code for the order.
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    /**
     * Convert the order to a human-readable string representation.
     *
     * @return A string representation of the order, including its unique identifier and status.
     */
    @Override
    public String toString() {
        return "Order: " + orderId + ", Status: " + orderStatus;
    }
}
