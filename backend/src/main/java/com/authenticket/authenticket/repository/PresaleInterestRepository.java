package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventUserId;
import com.authenticket.authenticket.model.PresaleInterest;
import com.authenticket.authenticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresaleInterestRepository extends JpaRepository<PresaleInterest, EventUserId> {
    List<PresaleInterest> findAllByEvent(Event event);
    List<PresaleInterest> findAllByUser(User user);
    List<PresaleInterest> findAllByEventAndIsSelected(Event event, Boolean isSelected);
    List<PresaleInterest> findAllByIsSelectedTrueAndEmailedFalse();
}
