package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


/**
 * Represents the type or category of an event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_type")
@JsonIgnoreProperties(value = { "createdAt", "deletedAt", "updatedAt" })
@EqualsAndHashCode(callSuper = true)

public class EventType extends BaseEntity {
    /**
     * The unique identifier for the event type, generated using an auto-increment strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer eventTypeId;

    /**
     * The name or label of the event type, which is a non-null field.
     */
    @Column(name = "type_name", nullable = false)
    private String eventTypeName;
}

