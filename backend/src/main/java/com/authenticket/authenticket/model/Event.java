package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Objects;

/**
 * The `Event` entity represents an event in the system.
 *
 * This entity is used to store information about events, including their name, description, date, and associated details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
@EqualsAndHashCode(callSuper = true)
public class Event extends BaseEntity {


    /**
     * The `ReviewStatus` enum represents the review status of an event.
     *
     * It provides three possible review statuses: "APPROVED," "PENDING," and "REJECTED."
     */
    public enum ReviewStatus {
        /**
         * The status indicating that the event has been approved.
         */
        APPROVED("approved"),

        /**
         * The status indicating that the event is pending review.
         */
        PENDING("pending"),

        /**
         * The status indicating that the event has been rejected.
         */
        REJECTED("rejected");

        private final String reviewStatus;

        /**
         * Creates a new `ReviewStatus` enum with the specified status value.
         *
         * @param statusValue The status value.
         */
        ReviewStatus(String statusValue) {
            this.reviewStatus = statusValue;
        }

        /**
         * Get the status value of the review status.
         *
         * @return The status value of the review status.
         */
        public String getStatusValue() {
            return reviewStatus;
        }
    }

    /**
     * The unique identifier for the event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    /**
     * The name of the event.
     */
    @Column(name = "event_name", nullable = false)
    private String eventName;

    /**
     * The description of the event (up to 2000 characters).
     */
    @Column(name = "event_description", nullable = false, length = 2000)
    private String eventDescription;


    /**
     * The date of the event.
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /**
     * Additional information about the event (up to 2000 characters).
     */
    @Column(name = "other_event_info", length = 2000)
    private String otherEventInfo;

    /**
     * The image associated with the event.
     */
    @Column(name = "event_image")
    private String eventImage;

    /**
     * The date when ticket sales start for the event.
     */
    @Column(name = "ticket_sale_date")
    private LocalDateTime ticketSaleDate;

    /**
     * The admin who reviewed the event (if applicable).
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="reviewed_by", referencedColumnName="admin_id")
    private Admin reviewedBy;

    /**
     * The review status of the event (e.g., "approved", "pending", "rejected").
     */
    @Column(name = "review_status")
    private String reviewStatus;

    /**
     * Remarks related to the review of the event.
     */
    @Column(name = "review_remarks")
    private String reviewRemarks;

    /**
     * Flag indicating whether the event is enhanced and has facial check-in enabled.
     */
    @Column(name = "is_enhanced")
    private Boolean isEnhanced;

    /**
     * Flag indicating whether the event has a presale period.
     */
    @Column(name = "has_presale")
    private Boolean hasPresale;

    /**
     * Flag indicating whether the event allows presale users.
     */
    @Column(name = "has_presale_users")
    private Boolean hasPresaleUsers;

    /**
     * The event organizer associated with this event.
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "organiser_id", nullable = false)
    private EventOrganiser organiser;

    /**
     * The venue where the event takes place.
     */
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    /**
     * The set of artists associated with this event.
     */
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    @JoinTable(
            schema = "dev",
            name = "artist_event",
            joinColumns = {@JoinColumn(name = "event_id")},
            inverseJoinColumns = {@JoinColumn(name = "artist_id")})
    private Set<Artist> artists;

    /**
     * The event type associated with this event.
     */
    @ManyToOne
    @JoinColumn(name="type_id",nullable = false)
    private EventType eventType;

    /**
     * The set of ticket pricings associated with this event.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<TicketPricing> ticketPricingSet = new HashSet<>();

    /**
     * The set of orders associated with this event.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Order> orderSet = new HashSet<>();

    /**
     * Get the set of artists associated with this event.
     *
     * @return The set of artists associated with this event.
     */
    @JsonIgnore
    public Set<Artist> getArtists(){
        return artists;
    }

    /**
     * Get the set of ticket pricings associated with this event.
     *
     * @return The set of ticket pricings associated with this event.
     */
    @JsonIgnore
    public Set<TicketPricing> getTicketPricingSet(){
        return ticketPricingSet;
    }

    /**
     * Add a new ticket pricing category and price to the event.
     *
     * @param ticketCategory The ticket category to add.
     * @param price The price for the ticket category.
     */
    public void addTicketPricing(TicketCategory ticketCategory, Double price) {
        TicketPricing ticketPricing = new TicketPricing(ticketCategory, this, price);
        ticketPricingSet.add(ticketPricing);
    }

    /**
     * Update the price for a ticket pricing category associated with the event.
     *
     * @param ticketPricing The ticket pricing category to update.
     * @param price The new price for the category.
     * @return `true` if the update was successful; `false` if the category was not found.
     */
    public boolean updateTicketPricing(TicketPricing ticketPricing, Double price) {
        for (TicketPricing ticketPricingIter : ticketPricingSet) {
            if (ticketPricingIter.equals(ticketPricing)) {
                ticketPricingIter.setPrice(price);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a ticket category from the event.
     *
     * @param ticketCategory The ticket category to remove.
     */
    public void removeTicketCategory(TicketCategory ticketCategory) {
        for (Iterator<TicketPricing> iterator = ticketPricingSet.iterator();
             iterator.hasNext(); ) {
            TicketPricing ticketPricing = iterator.next();

            if (ticketPricing.getEvent().equals(this) &&
                    ticketPricing.getCat().equals(ticketCategory)) {
                iterator.remove();
                ticketPricing.setEvent(null);
                ticketPricing.setCat(null);
            }
        }
    }

    /**
     * Calculate the hash code for the event based on its name.
     *
     * @return The hash code for the event.
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventName);
    }

}

