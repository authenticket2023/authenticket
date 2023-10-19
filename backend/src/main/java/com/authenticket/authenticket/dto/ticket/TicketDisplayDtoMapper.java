package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.dto.section.SectionDisplayDto;
import com.authenticket.authenticket.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for mapping Ticket entities to TicketDisplayDto.
 * {@link TicketDisplayDto} DTOs and performing updates on ticket entities.
 */
@Service
public class TicketDisplayDtoMapper implements Function<Ticket, TicketDisplayDto> {

    /**
     * Maps a Ticket entity to a TicketDisplayDto.
     *
     * @param ticket The Ticket entity to map.
     * @return A TicketDisplayDto representing the ticket information.
     */
    public TicketDisplayDto apply(Ticket ticket) {
        Integer orderId = null;
        if (ticket.getOrder() != null) {
            orderId = ticket.getOrder().getOrderId();
        }
        return new TicketDisplayDto(
                ticket.getTicketId(),
                ticket.getTicketPricing().getEvent().getEventId(),
                ticket.getTicketPricing().getCat().getCategoryId(),
                ticket.getSection().getSectionId(),
                ticket.getRowNo(),
                ticket.getSeatNo(),
                ticket.getTicketHolder(),
                orderId);
    }

    /**
     * Maps an Object array to a TicketDisplayDto.
     *
     * @param obj The Object array representing ticket information.
     * @return A TicketDisplayDto representing the ticket information.
     */
    public TicketDisplayDto applyTicketDisplayDto(Object[] obj) {
        return new TicketDisplayDto(
                (Integer) obj[0],
                (Integer) obj[1],
                (Integer) obj[2],
                (String) obj[3],
                (Integer) obj[4],
                (Integer) obj[5],
                (String) obj[6],
                (Integer) obj[7]
        );
    }

    /**
     * Maps a list of Object arrays to a list of TicketDisplayDto objects.
     *
     * @param ticketObjects The list of Object arrays representing ticket information.
     * @return A list of TicketDisplayDto objects representing the ticket information.
     */
    public List<TicketDisplayDto> mapTicketObjects(List<Object[]> ticketObjects) {
        return ticketObjects.stream()
                .map(this::applyTicketDisplayDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a list of Ticket entities to a set of TicketDisplayDto objects.
     *
     * @param allByOrder The list of Ticket entities to map.
     * @return A set of TicketDisplayDto objects representing the ticket information.
     */
    public Set<TicketDisplayDto> mapTicketDisplayDto(List<Ticket> allByOrder) {
        return allByOrder.stream()
                .map(this)
                .collect(Collectors.toSet());
    }
}

