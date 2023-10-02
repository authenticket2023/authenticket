package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket extends BaseEntity implements Comparable<Ticket> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="event_id", referencedColumnName="event_id", nullable = false),
            @JoinColumn(name="category_id", referencedColumnName="category_id", nullable = false)
    })
    private TicketPricing ticketPricing;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "row_no")
    private Integer rowNo;

    @Column(name = "seat_no")
    private Integer seatNo;

    @Column(name = "ticket_holder")
    private String ticketHolder;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnore
//    @JoinColumns({
//            @JoinColumn(name="event_id", referencedColumnName="event_id", nullable = false),
//            @JoinColumn(name="category_id", referencedColumnName="category_id", nullable = false)
//    })
//    private EventTicketCategory eventTicketCategory;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id")
    private Order order;
    @Override
    public int hashCode() {
        return Objects.hash(ticketId, ticketPricing, ticketHolder, order);
    }

    @Override
    public int compareTo(Ticket t) {
        return ticketId - t.getTicketId();
    }
}
