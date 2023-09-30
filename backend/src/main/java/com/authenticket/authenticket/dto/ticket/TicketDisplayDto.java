package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Event;

public record TicketDisplayDto(Integer ticketId,
                               Integer event_id,
                               Integer cat_id,
                               Integer section_id,
                               Integer row_no,
                               Integer seat_no,
                               String ticketHolder,
                               Integer orderId) {
}
