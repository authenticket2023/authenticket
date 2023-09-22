package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
import com.authenticket.authenticket.model.Artist;

import java.time.LocalDateTime;
import java.util.Set;

public record EventAdminDisplayDto(Integer eventId,
                                   String eventName,
                                   String eventDescription,
                                   LocalDateTime eventDate,
                                   LocalDateTime ticketSaleDate,
                                   String organiserEmail,
                                   String reviewRemarks,
                                   String reviewStatus,
                                   String reviewedBy,
                                   LocalDateTime deletedAt
) {
}
