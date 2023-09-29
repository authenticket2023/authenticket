package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.TicketPricing;
import com.authenticket.authenticket.model.EventTicketCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPricingRepository extends JpaRepository<TicketPricing, EventTicketCategoryId> {
}
