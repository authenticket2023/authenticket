package com.authenticket.authenticket.dto.order;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;

import java.time.LocalDate;
import java.util.Set;

public record OrderDisplayDto(
        Integer orderId,
        Double orderAmount,
        LocalDate purchaseDate,
        String orderStatus,
        UserDisplayDto purchaser,
        Set<TicketDisplayDto> ticketSet
) {
}



