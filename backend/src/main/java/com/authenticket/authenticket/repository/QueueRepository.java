package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, EventUserId> {
    // Get position in queue
    Integer countAllByEventAndCanPurchaseFalseAndTimeBeforeOrderByTime(Event event, LocalDateTime time);
    // Number of people purchasing
    Integer countAllByEventAndCanPurchase(Event event, Boolean canPurchase);
    // Find first person in queue
    Optional<Queue> findFirstByEventAndCanPurchaseFalseOrderByTimeAsc(Event event);
}
