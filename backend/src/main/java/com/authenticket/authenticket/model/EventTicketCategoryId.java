package com.authenticket.authenticket.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class EventTicketCategoryId implements Serializable {
    private TicketCategory cat;

    private Event event;
}
