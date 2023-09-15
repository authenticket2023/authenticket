package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.EventTicketCategory;

import java.time.LocalDateTime;
import java.util.Set;

public record FeaturedEventDto(Integer featuredId,
                              EventHomeDto event,
                              LocalDateTime startDate,
                               LocalDateTime endDate
) {
}