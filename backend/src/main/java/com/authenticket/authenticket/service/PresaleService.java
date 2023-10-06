package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventUserId;
import com.authenticket.authenticket.model.PresaleInterest;
import com.authenticket.authenticket.model.User;

import java.util.List;
import java.util.Optional;

public interface PresaleService {

    int MAX_TICKETS_SOLD_PER_USER = 5;

    List<User> findUsersInterestedByEvent(Event event);

    List<Event> findEventsByUser(User user);

    List<User> findUsersSelectedForEvent(Event event, Boolean selected);

    void selectPresaleUsersForEvent(Event event);

    void setPresaleInterest(User user, Event event, Boolean selected, Boolean emailed);

    void sendScheduledEmails();

    Optional<PresaleInterest> findPresaleInterestByID(EventUserId eventUserId);

    Boolean existsById(EventUserId eventUserId);
}
