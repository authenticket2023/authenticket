package com.authenticket.authenticket.dto.eventticketcategory;

import com.authenticket.authenticket.model.TicketPricing;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class provides mapping functionality for Event Ticket Category related objects.
 * {@link EventTicketCategoryDisplayDto} DTOs and performing updates on event ticket category entities.
 */
@Service
public class EventTicketCategoryDtoMapper implements Function<TicketPricing, EventTicketCategoryDisplayDto> {

    /**
     * Maps a TicketPricing object to an EventTicketCategoryDisplayDto.
     *
     * @param ticketPricing The TicketPricing object to be mapped.
     * @return The resulting EventTicketCategoryDisplayDto.
     */
    public EventTicketCategoryDisplayDto apply(TicketPricing ticketPricing) {
        return new EventTicketCategoryDisplayDto(
                ticketPricing.getCat().getCategoryId(),
                ticketPricing.getCat().getCategoryName(),
                ticketPricing.getPrice()
        );
    }

    /**
     * Updates the fields of a TicketPricing object based on the provided EventTicketCategoryUpdateDto.
     *
     * @param newEventTicketCategoryDto The EventTicketCategoryUpdateDto containing updated information.
     * @param oldTicketPricing          The TicketPricing object to be updated.
     */
    public void update(EventTicketCategoryUpdateDto newEventTicketCategoryDto, TicketPricing oldTicketPricing) {
        if (newEventTicketCategoryDto.price() != null) {
            oldTicketPricing.setPrice(newEventTicketCategoryDto.price());
        }
    }

    /**
     * Maps a set of TicketPricing objects to a set of EventTicketCategoryDisplayDtos.
     *
     * @param eventTicketObjects The set of TicketPricing objects to be mapped.
     * @return The resulting set of EventTicketCategoryDisplayDtos.
     */
    public Set<EventTicketCategoryDisplayDto> map(Set<TicketPricing> eventTicketObjects) {
        return eventTicketObjects.stream()
                .map(this::apply)
                .collect(Collectors.toSet());
    }
}

