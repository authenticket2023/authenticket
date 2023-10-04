package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Event;

public record TicketDisplayDto(Integer ticketId,
                               Integer eventId,
                               Integer catId,
                               String sectionId,
                               Integer rowNo,
                               Integer seatNo,
                               String ticketHolder,
                               Integer orderId) {
}
