package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "ticket")
public class Ticket extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;

        @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

//        @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "cat_id", nullable = false)
//    private TicketCategory ticketCategory;

    //    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "section_id", nullable = false)
//    private Section section;

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
//    @ManyToOne
//    @JsonIgnore
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;
}
