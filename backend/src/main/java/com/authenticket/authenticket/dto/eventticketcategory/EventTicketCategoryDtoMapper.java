package com.authenticket.authenticket.dto.eventticketcategory;

import com.authenticket.authenticket.model.TicketPricing;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventTicketCategoryDtoMapper implements Function<TicketPricing, EventTicketCategoryDisplayDto> {
    public EventTicketCategoryDisplayDto apply(TicketPricing ticketPricing) {

        return new EventTicketCategoryDisplayDto(
                ticketPricing.getCat().getCategoryId(), ticketPricing.getCat().getCategoryName(),
                ticketPricing.getPrice());
    }

    public void update(EventTicketCategoryUpdateDto newEventTicketCategoryDto, TicketPricing oldTicketPricing){
        if(newEventTicketCategoryDto.price() != null){
            oldTicketPricing.setPrice(newEventTicketCategoryDto.price());
        }
    }

    public Set<EventTicketCategoryDisplayDto> map(Set<TicketPricing> eventTicketObjects) {
        return eventTicketObjects.stream()
                .map(this::apply)
                .collect(Collectors.toSet());
    }
}
