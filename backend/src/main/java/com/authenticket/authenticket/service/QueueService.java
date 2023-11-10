package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.User;

public interface QueueService {
    // (20% of remaining tickets) number of users allowed to purchase at a time
    double PERCENT_OF_TICKETS_TO_ALLOW_USERS = 0.2;
    int getPosition(User user, Event event);
    int getTotalInQueue(Event event);
    boolean canPurchase(User user, Event event);
    void addToQueue(User user, Event event);
    void updatePurchasingUsersInQueue(Event event);
    void removeFromQueue(User user, Event event);
//    Event findIfUserQueuing(User user);
}
