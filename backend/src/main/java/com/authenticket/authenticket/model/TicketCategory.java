package com.authenticket.authenticket.model;


import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "ticket_categories", schema = "dev")
public class TicketCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "price")
    private Double price;

    @Column(name = "available_tickets")
    private Integer availableTickets;
}
