package com.authenticket.authenticket.dto.ticketcategory;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.TicketCategory;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TicketCategoryDisplayDtoMapper implements Function<TicketCategory, TicketCategoryDisplayDto> {
    public TicketCategoryDisplayDto apply(TicketCategory ticketCategory) {
        return new TicketCategoryDisplayDto(
                ticketCategory.getCategoryId(), ticketCategory.getEvent().getEventId(), ticketCategory.getCategoryName(),
                ticketCategory.getPrice(), ticketCategory.getAvailableTickets());
    }

    public void update(TicketCategoryUpdateDto dto, TicketCategory ticketCategory) {
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
