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

/**
 * This class implements the PresaleService interface and provides methods for managing presale interests
 * for events. It allows users to express interest in events, selects users for presale, and sends email notifications
 * to selected users for early ticket sales.
 *
 */
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

    /**
     * Finds a presale interest record by its composite key, represented by an EventUserId.
     *
     * @param eventUserId The composite key consisting of User and Event for the presale interest.
     * @return An Optional containing the presale interest, or an empty Optional if not found.
     */
    @Override
    public Optional<PresaleInterest> findPresaleInterestByID(EventUserId eventUserId) {
        return presaleInterestRepository.findById(eventUserId);
    }

    /**
     * Checks if a presale interest record with the given EventUserId composite key exists.
     *
     * @param id The composite key to check for existence.
     * @return true if a matching presale interest record exists, false otherwise.
     */
    @Override
    public Boolean existsById(EventUserId id) {
        return presaleInterestRepository.findById(id).isPresent();
    }

    /**
     * Finds a list of users who have expressed interest in a specific event.
     *
     * @param event The event for which to find interested users.
     * @return A list of users interested in the event.
     */
    @Override
    public List<User> findUsersInterestedByEvent(Event event) {
        return presaleInterestRepository.findAllByEvent(event).stream().map(PresaleInterest::getUser).toList();
    }

    /**
     * Finds a list of events for which a specific user has expressed interest.
     *
     * @param user The user for whom to find the interested events.
     * @return A list of events in which the user is interested.
     */
    @Override
    public List<Event> findEventsByUser(User user) {
        return presaleInterestRepository.findAllByUser(user).stream().map(PresaleInterest::getEvent).toList();
    }

    /**
     * Finds a list of users selected for a specific event's presale.
     *
     * @param event The event for which to find selected users.
     * @param selected true to find users who are selected, false to find those who are not.
     * @return A list of users selected or not selected for the event's presale.
     */
    @Override
    public List<User> findUsersSelectedForEvent(Event event, Boolean selected) {
        return presaleInterestRepository.findAllByEventAndIsSelected(event, selected).stream().map(PresaleInterest::getUser).toList();
    }

    /**
     * Sets a user's presale interest for an event, indicating whether they are selected and emailed.
     *
     * @param user     The user expressing interest.
     * @param event    The event of interest.
     * @param selected true if the user is selected, false otherwise.
     * @param emailed  true if an email was sent, false otherwise.
     * @throws AlreadyExistsException If the user has already expressed interest in the event.
     */
    @Override
    public void setPresaleInterest(User user, Event event, Boolean selected, Boolean emailed){
        if (!selected && existsById(new EventUserId(user, event))) {
            throw new AlreadyExistsException("User with ID " + user.getUserId() + " already expressed interest for event '" + event.getEventName() + "'");
        }
        presaleInterestRepository.save(new PresaleInterest(user, event, selected, emailed));
    }

    /**
     * Randomly selects a certain number of users for a specific event's presale based on the number of available tickets.
     *
     * @param event The event for which to select users for the presale.
     */
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

    /**
     * Sends scheduled email notifications to selected users, two at a time for early ticket sales.
     * This method runs periodically based on a fixed rate, currently set at 1 minute.
     */
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

    /**
     * Sends an email notification to a user alerting them about the early ticket sale.
     *
     * @param presaleInterest The presale interest information.
     */
    private void sendUserAlert(PresaleInterest presaleInterest) {
        Event event = presaleInterest.getEvent();
        User user = presaleInterest.getUser();
        System.out.println("Sending email to: " + user.getName());
        emailService.send(user.getEmail(), EmailServiceImpl.buildEarlyTicketSaleNotificationEmail(presaleInterest.getUser().getName(), event.getEventName(), apiUrl + "/EventDetails/" + event.getEventId(), event.getTicketSaleDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss a"))), "Ticket Presale Notification");

        presaleInterest.setEmailed(true);
        presaleInterestRepository.save(presaleInterest);
    }
}
