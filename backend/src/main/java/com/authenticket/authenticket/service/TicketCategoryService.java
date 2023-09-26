package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.model.TicketCategory;

import java.util.List;
import java.util.Optional;

public interface TicketCategoryService {
    List<TicketCategoryDisplayDto> findAllTicketCategory();
    Optional<TicketCategoryDisplayDto> findTicketCategoryById(Integer categoryId);
    TicketCategory saveTicketCategory(String name);
    TicketCategory updateTicketCategory(Integer categoryId, String name);
    void deleteTicket(Integer categoryId);
    void removeTicketCategory(Integer categoryId);
}
