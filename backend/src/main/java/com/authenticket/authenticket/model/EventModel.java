package com.authenticket.authenticket.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventTest", schema = "dev")
public class EventModel extends BaseModel {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    //    @ManyToOne
//    @JoinColumn(name = "organiser_id", referencedColumnName = "organiser_id")
//    private EventOrganiser organiser;
    @Getter
    @Setter
    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Getter
    @Setter
    @Column(name = "event_description")
    private String eventDescription;

    @Getter
    @Setter
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Getter
    @Setter
    @Column(name = "event_location")
    private String eventLocation;

    @Getter
    @Setter
    @Column(name = "other_event_info")
    private String otherEventInfo;

//    @Column(name = "event_image", columnDefinition = "VARCHAR[]")
//    private String[] eventImage;

    //Constructors
    public EventModel() {

    }

    public EventModel(Long eventId, String eventName, String eventDescription, LocalDateTime eventDate, String eventLocation, String otherEventInfo) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.otherEventInfo = otherEventInfo;
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

