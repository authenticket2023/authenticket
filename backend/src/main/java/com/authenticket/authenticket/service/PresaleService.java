package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.User;

import java.util.List;

public interface PresaleService {

    public static final int MAX_TICKETS_SOLD_PER_USER = 5;

    public List<User> findUsersInterestedByEvent(Event event);

    public List<User> findUsersSelectedForEvent(Event event, Boolean selected);

    public List<User> selectPresaleUsersForEvent(Event event);

    void setPresaleInterest(User user, Event event, Boolean selected, Boolean emailed);

    void sendScheduledEmails();
}
