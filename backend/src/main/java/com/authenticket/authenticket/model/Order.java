package com.authenticket.authenticket.model;

import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order")
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity{

    public enum Status {
        PROCESSING("Processing"),
        SUCCESS("Success"),
        CANCELLED("Cancelled");

        private final String statusValue;

        Status(String statusValue) {
            this.statusValue = statusValue;
        }

        public String getStatusValue() {
            return statusValue;
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_amount", nullable = false)
    private Double orderAmount;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private Set<Ticket> ticketSet = new HashSet<>();

    @JsonIgnore
    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    public void addTicket(Ticket ticket) {
        ticketSet.add(ticket);
    }

    public boolean updateTicket(Ticket ticket, String ticketHolder) {
        for (Ticket ticketIter : ticketSet) {
            if (ticketIter.equals(ticket)) {
                ticketIter.setTicketHolder(ticketHolder);
                return true;
            }
        }
        return false;
    }

    public void removeTicket(Integer ticketId) {
        for (Iterator<Ticket> iterator = ticketSet.iterator();
             iterator.hasNext(); ) {
            Ticket ticket = iterator.next();

            if (ticket.getOrder().equals(this) &&
                    ticket.getTicketId().equals(ticketId)) {
                iterator.remove();
            }
        }
    }
}
