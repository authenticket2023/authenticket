package com.authenticket.authenticket.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class EventTicketCategoryId implements Serializable {
    private TicketCategory cat_id;

    private Event event_id;
}
