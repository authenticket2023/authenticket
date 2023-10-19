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

    // (20% of remaining tickets) number of users allowed to purchase at a time
    private int getNumberOfUsersAllowed(Event event) {
        int totalSeats = venueRepository.findNoOfSeatsByVenue(event.getVenue().getVenueId());
        int sold = ticketRepository.countTicketsByOrder_Event(event);
        int remainingTickets = totalSeats - sold;
        return (int) Math.ceil(remainingTickets * PERCENT_OF_TICKETS_TO_ALLOW_USERS);
    }

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

    @Override
    public int getTotalInQueue(Event event) {
        return queueRepository.countAllByEventAndCanPurchase(event, false);
    }

    @Override
    public boolean canPurchase(User user, Event event) {
        EventUserId id = new EventUserId(user, event);
        Optional<Queue> queueOptional = queueRepository.findById(id);
        if (queueOptional.isEmpty()) {
            throw new NonExistentException("Queue", id);
        }

        return queueOptional.get().getCanPurchase();
    }

    @Override
    public void addToQueue(User user, Event event) {
        if (queueRepository.existsById(new EventUserId(user, event))) {
            throw new AlreadyExistsException("User already in queue");
        }

        queueRepository.save(new Queue(user, event, false, LocalDateTime.now()));
        updatePurchasingUsersInQueue(event);
    }

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
