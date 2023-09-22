package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.EventOrganiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventOrganiserRepository extends JpaRepository<EventOrganiser, Integer> {
    Optional<EventOrganiser> findByEmail(String email);
    List<EventOrganiser> findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(String pending);
}

