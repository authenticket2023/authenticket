package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;

import java.util.List;
import java.util.Optional;

public interface PresaleService {
    public List<User> findUsersInterestedByEvent(Integer eventId);

    public void addPresaleInterest(Integer userId, Integer eventId);
}
