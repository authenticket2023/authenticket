package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_description", nullable = false, length = 2000)
    private String eventDescription;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "other_event_info", length = 2000)
    private String otherEventInfo;

    @Column(name = "event_image")
    private String eventImage;

    @Column(name = "ticket_sale_date")
    private LocalDateTime ticketSaleDate;

    @Column(name = "total_tickets")
    private Integer totalTickets;

    @Column(name = "total_tickets_sold")
    private Integer totalTicketsSold;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="reviewed_by", referencedColumnName="admin_id")
    private Admin reviewedBy;

    @Column(name = "review_status")
    private String reviewStatus;

    @Column(name = "review_remarks")
    private String reviewRemarks;

    @Column(name = "is_enhanced")
    private Boolean isEnhanced;

    @Column(name = "has_presale")
    private Boolean hasPresale;

    @Column(name = "has_presale_users")
    private Boolean hasPresaleUsers;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "organiser_id", nullable = false)
    private EventOrganiser organiser;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

//    @Getter
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    @JoinTable(
            schema = "dev",
            name = "artist_event",
            joinColumns = {@JoinColumn(name = "event_id")},
            inverseJoinColumns = {@JoinColumn(name = "artist_id")})
    private Set<Artist> artists;

    @ManyToOne
    @JoinColumn(name="type_id",nullable = false)
    private EventType eventType;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    Set<TicketPricing> ticketPricingSet = new HashSet<>();

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
////    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
//    Set<Ticket> ticketSet = new HashSet<>();

    @JsonIgnore
    public Set<Artist> getArtists(){
        return artists;
    }

    @JsonIgnore
    public Set<TicketPricing> getTicketPricingSet(){
        return ticketPricingSet;
    }

    public void addTicketPricing(TicketCategory ticketCategory, Double price) {
        TicketPricing ticketPricing = new TicketPricing(ticketCategory, this, price);
        ticketPricingSet.add(ticketPricing);
    }

    public boolean updateTicketPricing(TicketPricing ticketPricing, Double price) {
        for (TicketPricing ticketPricingIter : ticketPricingSet) {
            if (ticketPricingIter.equals(ticketPricing)) {
                ticketPricingIter.setPrice(price);
                return true;
            }
        }
        return false;
    }

    public void removeTicketCategory(TicketCategory ticketCategory) {
        for (Iterator<TicketPricing> iterator = ticketPricingSet.iterator();
             iterator.hasNext(); ) {
            TicketPricing ticketPricing = iterator.next();

            if (ticketPricing.getEvent().equals(this) &&
                    ticketPricing.getCat().equals(ticketCategory)) {
                iterator.remove();
//                eventTicketCategory.getCat_id().getEventTicketCategorySet().remove(eventTicketCategory);
                ticketPricing.setEvent(null);
                ticketPricing.setCat(null);
            }
        }
    }



    @Override
    public int hashCode() {
        return Objects.hash(eventName);
    }
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinTable(name = "event_ticket_category", joinColumns = {@JoinColumn(table = "event",
//            name = "event_id",
//            referencedColumnName = "event_id"),
//            @JoinColumn(table = "ticket_categories",
//                    name = "category_id",
//                    referencedColumnName = "category_id")},
//            inverseJoinColumns = {@JoinColumn(table = "event",
//                    name="event_id",
//                    referencedColumnName = "event_id")})
//    Set<EventTicketCategory> eventTicketCategorySet = new HashSet<>();

    //    @ManyToOne(fetch = FetchType.EAGER)
//    @JsonIgnore
//    @JoinColumn(name = "venue_id")
//    private Venue venue;

//    //https://www.baeldung.com/jpa-many-to-many
//    @ManyToMany
//    private ArrayList<Artist> artistList;
//
//
//    public Event(Integer eventId, String eventName, String eventDescription, LocalDateTime eventDate, String eventLocation, String otherEventInfo, String eventImage, LocalDateTime ticketSaleDate, EventOrganiser eventOrganiser) {
//    }
}

