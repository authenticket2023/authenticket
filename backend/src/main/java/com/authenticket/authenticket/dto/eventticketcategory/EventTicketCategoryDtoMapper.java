package com.authenticket.authenticket.dto.eventticketcategory;

import com.authenticket.authenticket.dto.venue.VenueUpdateDto;
import com.authenticket.authenticket.model.EventTicketCategory;
import com.authenticket.authenticket.model.Venue;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventTicketCategoryDtoMapper implements Function<EventTicketCategory, EventTicketCategoryDisplayDto> {
    public EventTicketCategoryDisplayDto apply(EventTicketCategory eventTicketCategory) {

        return new EventTicketCategoryDisplayDto(
                eventTicketCategory.getCat().getCategoryId(), eventTicketCategory.getCat().getCategoryName(),
                eventTicketCategory.getPrice(),eventTicketCategory.getAvailableTickets(),
                eventTicketCategory.getTotalTicketsPerCat());
    }

    public void update(EventTicketCategoryUpdateDto newEventTicketCategoryDto, EventTicketCategory oldEventTicketCategory){
        if(newEventTicketCategoryDto.availableTickets() != null){
            oldEventTicketCategory.setAvailableTickets(newEventTicketCategoryDto.availableTickets());
        }
        if(newEventTicketCategoryDto.totalTicketsPerCat() != null){
            oldEventTicketCategory.setTotalTicketsPerCat(newEventTicketCategoryDto.totalTicketsPerCat());
        }
        if(newEventTicketCategoryDto.price() != null){
            oldEventTicketCategory.setPrice(newEventTicketCategoryDto.price());
        }
    }

    public Set<EventTicketCategoryDisplayDto> map(Set<EventTicketCategory> eventObjects) {
        return eventObjects.stream()
                .map(this::apply)
                .collect(Collectors.toSet());
    }
}
