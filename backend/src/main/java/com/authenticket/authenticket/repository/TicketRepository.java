package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByTicketPricing_Event_EventId(Integer eventId);
}
