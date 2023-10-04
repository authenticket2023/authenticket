package com.authenticket.authenticket.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class EventUserId implements Serializable {
    private User user;
    private Event event;
}
