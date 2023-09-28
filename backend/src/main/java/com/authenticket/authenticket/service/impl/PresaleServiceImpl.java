package com.authenticket.authenticket.service.impl;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import com.authenticket.authenticket.TicketCategoryJSON;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.PresaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PresaleServiceImpl implements PresaleService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final PresaleInterestRepository presaleInterestRepository;

    @Autowired
    public PresaleServiceImpl(UserRepository userRepository,
                              EventRepository eventRepository,
                              PresaleInterestRepository presaleInterestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.presaleInterestRepository = presaleInterestRepository;
    }

    public List<User> findUsersInterestedByEvent(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        return presaleInterestRepository.findAllByEvent(eventOptional.get()).stream().map(x->x.getUser()).toList();
    }

    public void addPresaleInterest(Integer userId, Integer eventId){
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }

        if (userOptional.isEmpty()){
            throw new NonExistentException("User", userId);
        }
        Event event = eventOptional.get();
        User user = userOptional.get();

        EventUserId eventUserId = new EventUserId(user, event);
        Optional<PresaleInterest> presaleInterestOptional = presaleInterestRepository.findById(eventUserId);

        if (presaleInterestOptional.isEmpty()){
            presaleInterestRepository.save(new PresaleInterest(user, event, false));
            return;
        }

        throw new AlreadyExistsException("User ID " + userId + " already expressed interest for event ID " + eventId);
    }
}
