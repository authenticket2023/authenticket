package com.authenticket.authenticket.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * A composite key class used to represent the relationship between a TicketCategory and an Event.
 * This composite key is used to uniquely identify a combination of TicketCategory and Event.
 */
@NoArgsConstructor
@AllArgsConstructor
public class EventTicketCategoryId implements Serializable {
    /**
     * The TicketCategory associated with this composite key.
     */
    private TicketCategory cat;

    /**
     * The Event associated with this composite key.
     */
    private Event event;

    /**
     * Returns a string representation of this composite key.
     *
     * @return A string representation in the format "{ Event ID: [Event ID], Category ID: [Category ID] }".
     */
    @Override
    public String toString(){
        return "{ Event ID: " + event.getEventId() + ", Category ID: " + cat.getCategoryId() + " }";
    }
}
