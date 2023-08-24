package com.authenticket.authenticket.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "eventTest", schema = "dev")
@Data
@Builder
public class EventModel extends BaseModel {
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

    //Constructors

    public EventModel(Long eventId, String eventName, String eventDescription, LocalDateTime eventDate, String eventLocation, String otherEventInfo) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.otherEventInfo = otherEventInfo;
    }

    public EventModel() {

    }


    //To String
    @Override
    public String toString() {
        return "EventModel{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", eventDate=" + eventDate +
                ", eventLocation='" + eventLocation + '\'' +
                ", otherEventInfo='" + otherEventInfo + '\'' +
                '}';
    }

    // Other methods
}

