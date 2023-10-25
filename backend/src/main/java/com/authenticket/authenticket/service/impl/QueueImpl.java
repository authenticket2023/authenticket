package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventUserId;
import com.authenticket.authenticket.model.Queue;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.QueueRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.QueueService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service implementation for managing the queue of users purchasing tickets for events.
 */
@Service
public class QueueImpl implements QueueService {

    private final QueueRepository queueRepository;
    private final VenueRepository venueRepository;
    private final TicketRepository ticketRepository;

    public QueueImpl(QueueRepository queueRepository,
                     VenueRepository venueRepository,
                     TicketRepository ticketRepository) {
        this.queueRepository = queueRepository;
        this.venueRepository = venueRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Calculates the number of users allowed to purchase tickets at a time based on the remaining available tickets.
     *
     * @param event The event for which to determine the number of allowed users.
     * @return The number of users allowed to purchase tickets at a time.
     */
    private int getNumberOfUsersAllowed(Event event) {
        int totalSeats = venueRepository.findNoOfSeatsByVenue(event.getVenue().getVenueId());
        int sold = ticketRepository.countTicketsByOrder_Event(event);
        int remainingTickets = totalSeats - sold;
        return (int) Math.ceil(remainingTickets * PERCENT_OF_TICKETS_TO_ALLOW_USERS);
    }

    /**
     * Retrieves the position of a user in the queue for a specific event.
     *
     * @param user  The user for whom to find the queue position.
     * @param event The event in question.
     * @return The user's queue position, or -1 if the user is not in the queue.
     */
    @Override
    public int getPosition(User user, Event event) {
        Optional<Queue> queueOptional = queueRepository.findById(new EventUserId(user, event));
        if (queueOptional.isEmpty()) {
            return -1;
        }

        Queue queue = queueOptional.get();
        if (queue.getCanPurchase()) {
            return 0;
        }

        // plus 1 to count self
        return queueRepository.countAllByEventAndCanPurchaseFalseAndTimeBeforeOrderByTime(event, queue.getTime()) + 1;
    }

    /**
     * Retrieves the total number of users in the queue for a specific event.
     *
     * @param event The event for which to count the users in the queue.
     * @return The total number of users in the queue for the event.
     */
    @Override
    public int getTotalInQueue(Event event) {
        return queueRepository.countAllByEventAndCanPurchase(event, false);
    }

    /**
     * Checks if a user is allowed to purchase tickets for a specific event.
     *
     * @param user  The user to check.
     * @param event The event in question.
     * @return true if the user can purchase tickets, false otherwise.
     */
    @Override
    public boolean canPurchase(User user, Event event) {
        EventUserId id = new EventUserId(user, event);
        Optional<Queue> queueOptional = queueRepository.findById(id);
        if (queueOptional.isEmpty()) {
            throw new NonExistentException("Queue", id);
        }

        return queueOptional.get().getCanPurchase();
    }

    /**
     * Adds a user to the queue for a specific event.
     *
     * @param user  The user to add to the queue.
     * @param event The event to which the user is being added to the queue.
     */
    @Override
    public void addToQueue(User user, Event event) {
        if (queueRepository.existsById(new EventUserId(user, event))) {
            throw new AlreadyExistsException("User already in queue");
        }

        queueRepository.save(new Queue(user, event, false, LocalDateTime.now()));
        updatePurchasingUsersInQueue(event);
    }

    /**
     * Updates the users in the queue who can start purchasing tickets for an event.
     * This method is called based on predefined criteria.
     *
     * @param event The event for which to update the users in the queue.
     */
    @Override
    public void updatePurchasingUsersInQueue(Event event) {
        if (!LocalDateTime.now().isAfter(event.getTicketSaleDate())) {
            return;
        }

        int totalUsersAllowed = getNumberOfUsersAllowed(event);
        int numUsersPurchasing = queueRepository.countAllByEventAndCanPurchase(event, true);
        if (totalUsersAllowed > numUsersPurchasing) {
            for (int i = 0; i < totalUsersAllowed - numUsersPurchasing; i++) {
                Optional<Queue> queueOptional = queueRepository.findFirstByEventAndCanPurchaseFalseOrderByTimeAsc(event);
                if (queueOptional.isEmpty()) {
                    return;
                }
                Queue queue = queueOptional.get();
                queue.setCanPurchase(true);
                queueRepository.save(queue);
            }
        }
    }

    /**
     * Removes a user from the queue for a specific event.
     *
     * @param user  The user to remove from the queue.
     * @param event The event from which the user is being removed.
     */
    @Override
    public void removeFromQueue(User user, Event event) {
        Optional<Queue> queueOptional = queueRepository.findById(new EventUserId(user, event));
        if (queueOptional.isEmpty()) {
            throw new NonExistentException("Cannot remove from queue as it does not exist");
        }
        Queue queue = queueOptional.get();

        queueRepository.delete(queue);
        if (queue.getCanPurchase()) {
            updatePurchasingUsersInQueue(event);
        }
    }

//    @Override
//    public Event findIfUserQueuing(User user) {
//        queueRepository.f
//        return null;
//    }
}
