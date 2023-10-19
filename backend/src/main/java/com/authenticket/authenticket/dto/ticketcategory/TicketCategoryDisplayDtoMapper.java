package com.authenticket.authenticket.dto.ticketcategory;

import com.authenticket.authenticket.model.TicketCategory;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * A mapper class for converting TicketCategory entities to TicketCategoryDisplayDto objects.
 */
@Service
public class TicketCategoryDisplayDtoMapper implements Function<TicketCategory, TicketCategoryDisplayDto> {

    /**
     * Converts a TicketCategory entity to a TicketCategoryDisplayDto object.
     *
     * @param ticketCategory The TicketCategory entity to be converted.
     * @return A TicketCategoryDisplayDto object representing the same data.
     */
    public TicketCategoryDisplayDto apply(TicketCategory ticketCategory) {
        return new TicketCategoryDisplayDto(
                ticketCategory.getCategoryId(), ticketCategory.getCategoryName());
    }

    /**
     * Updates a TicketCategory entity based on the data in a TicketCategoryUpdateDto.
     *
     * @param dto           The TicketCategoryUpdateDto containing the updated data.
     * @param ticketCategory The TicketCategory entity to be updated.
     */
    public void update(TicketCategoryUpdateDto dto, TicketCategory ticketCategory) {
        if (dto.categoryName() != null) {
            ticketCategory.setCategoryName(dto.categoryName());
        }
    }
}

