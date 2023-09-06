package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.EventTicketCategory;
import com.authenticket.authenticket.model.EventTicketCategoryId;
import com.authenticket.authenticket.model.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTicketCategoryRepository extends JpaRepository<EventTicketCategory, EventTicketCategoryId> {
}
