package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.EmailService;
import com.authenticket.authenticket.service.PresaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
@Service
public class PresaleServiceImpl implements PresaleService {

    private final UserRepository userRepository;

    private final VenueRepository venueRepository;

    private final EventRepository eventRepository;

    private final PresaleInterestRepository presaleInterestRepository;

    private final EmailService emailService;

    @Value("${authenticket.frontend-production-url}")
    private String apiUrl;

    @Autowired
    public PresaleServiceImpl(UserRepository userRepository,
                              EventRepository eventRepository,
                              PresaleInterestRepository presaleInterestRepository,
                              VenueRepository venueRepository,
                              EmailService emailService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.presaleInterestRepository = presaleInterestRepository;
        this.venueRepository = venueRepository;
        this.emailService = emailService;
    }

    @Override
    public Optional<PresaleInterest> findPresaleInterestByID(EventUserId eventUserId) {
        return presaleInterestRepository.findById(eventUserId);
    }

    @Override
    public Boolean existsById(EventUserId id) {
        return presaleInterestRepository.findById(id).isPresent();
    }

    @Override
    public List<User> findUsersInterestedByEvent(Event event) {
        return presaleInterestRepository.findAllByEvent(event).stream().map(PresaleInterest::getUser).toList();
    }

    @Override
    public List<Event> findEventsByUser(User user) {
        return presaleInterestRepository.findAllByUser(user).stream().map(PresaleInterest::getEvent).toList();
    }

    @Override
    public List<User> findUsersSelectedForEvent(Event event, Boolean selected) {
        return presaleInterestRepository.findAllByEventAndIsSelected(event, selected).stream().map(PresaleInterest::getUser).toList();
    }

    @Override
    public void setPresaleInterest(User user, Event event, Boolean selected, Boolean emailed){
        if (!selected && existsById(new EventUserId(user, event))) {
            throw new AlreadyExistsException("User with ID " + user.getUserId() + " already expressed interest for event '" + event.getEventName() + "'");
        }
        presaleInterestRepository.save(new PresaleInterest(user, event, selected, emailed));
    }

    @Override
    public void selectPresaleUsersForEvent(Event event) {
        List<User> users = findUsersInterestedByEvent(event);
        int totalTickets = venueRepository.findNoOfSeatsByVenue(event.getVenue().getVenueId());
        int presaleWinnersCount = users.size();

        if (presaleWinnersCount * MAX_TICKETS_SOLD_PER_USER > totalTickets) {
            presaleWinnersCount = totalTickets / MAX_TICKETS_SOLD_PER_USER;
        }

        Random rand = new Random();

        ArrayList<User> duplicate = new ArrayList<>(users);
        for (int i = 0; i < presaleWinnersCount; i++) {

            // take a random index between 0 to size
            // of given List
            int randomIndex = rand.nextInt(duplicate.size());

            setPresaleInterest(duplicate.get(randomIndex), event, true, false);

            //Trigger Email sending interval


            // Remove selected element from original list
            duplicate.remove(randomIndex);
        }
        event.setHasPresaleUsers(true);
        eventRepository.save(event);
    }

    private static final int SECONDS_PER_INTERVAL = 60;

    private static final int NO_OF_EMAIL_PER_INTERVAL = 2;

    @Scheduled(fixedRate = 1000 * SECONDS_PER_INTERVAL)
    @Override
    public void sendScheduledEmails() {
        List<PresaleInterest> presaleInterestList = presaleInterestRepository.findAllByIsSelectedTrueAndEmailedFalse();

        int iterCount = NO_OF_EMAIL_PER_INTERVAL;
        if (presaleInterestList.size() < NO_OF_EMAIL_PER_INTERVAL) {
            iterCount = presaleInterestList.size();
        }

        for (int i = 0; i < iterCount; i++){
            sendUserAlert(presaleInterestList.get(i));
        }
    }

    private void sendUserAlert(PresaleInterest presaleInterest) {
        Event event = presaleInterest.getEvent();
        User user = presaleInterest.getUser();
        System.out.println("Sending email to: " + user.getName());
        emailService.send(user.getEmail(), EmailServiceImpl.buildEarlyTicketSaleNotificationEmail(presaleInterest.getUser().getName(), event.getEventName(), apiUrl + "/EventDetails/" + event.getEventId(), event.getTicketSaleDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss a"))), "Ticket Presale Notification");

        presaleInterest.setEmailed(true);
        presaleInterestRepository.save(presaleInterest);
    }
}
