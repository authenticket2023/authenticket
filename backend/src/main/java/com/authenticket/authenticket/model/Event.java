package com.authenticket.authenticket.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_test", schema = "dev")
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    //    @ManyToOne
//    @JoinColumn(name = "organiser_id", referencedColumnName = "organiser_id")
//    private EventOrganiser organiser;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_description")
    private String eventDescription;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "event_location")
    private String eventLocation;

    @Column(name = "other_event_info")
    private String otherEventInfo;

//    @Column(name = "event_image", columnDefinition = "VARCHAR[]")
//    private String[] eventImage;
    // Other methods
}

