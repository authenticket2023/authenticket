package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketDisplayDtoMapper implements Function<Ticket, TicketDisplayDto> {

    public TicketDisplayDto apply(Ticket ticket) {
        Integer orderId = null;
        if(ticket.getOrder() != null){
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

    public TicketDisplayDto applyTicketDisplayDto(Object[] obj){
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


    public List<TicketDisplayDto> mapTicketObjects(List<Object[]> ticketObjects) {

        return ticketObjects.stream()
                .map(this::applyTicketDisplayDto)
                .collect(Collectors.toList());
    }

    public Set<TicketDisplayDto> mapTicketDisplayDto(List<Ticket> allByOrder) {
        return allByOrder.stream()
                .map(this)
                .collect(Collectors.toSet());
    }
//
//    public void update(TicketUpdateDto dto, Ticket ticket) {
//        if (dto.userId() != null) {
//            User user = userRepository.findById(dto.userId()).orElse(null);
//            ticket.setUser(user);
//        }
//    }


}
