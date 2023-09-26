package com.authenticket.authenticket.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_type")
@EqualsAndHashCode(callSuper = true)
public class EventType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer eventTypeId;

    @Column(name = "type_name", nullable = false)
    private String eventTypeName;
}

