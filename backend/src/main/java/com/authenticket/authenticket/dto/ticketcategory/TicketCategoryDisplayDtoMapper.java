package com.authenticket.authenticket.dto.ticketcategory;

import com.authenticket.authenticket.model.TicketCategory;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TicketCategoryDisplayDtoMapper implements Function<TicketCategory, TicketCategoryDisplayDto> {
    public TicketCategoryDisplayDto apply(TicketCategory ticketCategory) {
        return new TicketCategoryDisplayDto(
                ticketCategory.getCategoryId(), ticketCategory.getCategoryName());
    }

    public void update(TicketCategoryUpdateDto dto, TicketCategory ticketCategory) {
        if (dto.categoryName() != null) {
            ticketCategory.setCategoryName(dto.categoryName());
        }
    }
}
