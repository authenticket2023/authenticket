package com.authenticket.authenticket.dto.ticketcategory;

import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketCategoryUpdateDtoMapper {

    public void apply(TicketCategoryUpdateDto dto, TicketCategory ticketCategory) {
        if (dto.availableTickets() != null) {
            ticketCategory.setAvailableTickets(dto.availableTickets());
        }
        if (dto.categoryName() != null) {
            ticketCategory.setCategoryName(dto.categoryName());
        }
        if (dto.price() != null) {
            ticketCategory.setPrice(dto.price());
        }
    }
}
