package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventOrganiserRepository extends JpaRepository<EventOrganiser, Integer> {
    Optional<EventOrganiser> findByEmail(String email);
    List<EventOrganiser> findByReviewStatusContains(String pending);
}

