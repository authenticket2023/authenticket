package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TicketDisplayDtoMapper implements Function<Ticket, TicketDisplayDto> {
    public TicketDisplayDto apply(Ticket ticket) {
        return new TicketDisplayDto(
                ticket.getTicketId(), ticket.getUser().getUser_id(), ticket.getEvent().getEventId(),
                ticket.getCategory().getCategoryId());
    }
}
