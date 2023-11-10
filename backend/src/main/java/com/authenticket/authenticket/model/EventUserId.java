package com.authenticket.authenticket.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * A composite key class used to represent the relationship between a User and an Event.
 * This composite key is used to uniquely identify a combination of User and Event.
 */
@NoArgsConstructor
@AllArgsConstructor
public class EventUserId implements Serializable {
    /**
     * The User associated with this composite key.
     */
    private User user;

    /**
     * The Event associated with this composite key.
     */
    private Event event;
}
