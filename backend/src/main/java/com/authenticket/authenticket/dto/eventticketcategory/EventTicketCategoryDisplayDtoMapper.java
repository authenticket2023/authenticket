package com.authenticket.authenticket.dto.eventticketcategory;

import com.authenticket.authenticket.dto.event.ArtistEventDto;
import com.authenticket.authenticket.model.EventTicketCategory;
import com.authenticket.authenticket.model.TicketCategory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventTicketCategoryDisplayDtoMapper implements Function<EventTicketCategory, EventTicketCategoryDisplayDto> {
    public EventTicketCategoryDisplayDto apply(EventTicketCategory eventTicketCategory) {

        return new EventTicketCategoryDisplayDto(
                eventTicketCategory.getCat().getCategoryId(), eventTicketCategory.getCat().getCategoryName(),
                eventTicketCategory.getPrice(),eventTicketCategory.getAvailableTickets(),
                eventTicketCategory.getTotalTicketsPerCat());
    }


    public Set<EventTicketCategoryDisplayDto> map(Set<EventTicketCategory> eventObjects) {
        return eventObjects.stream()
                .map(this::apply)
                .collect(Collectors.toSet());
    }
}
