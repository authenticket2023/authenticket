package com.authenticket.authenticket.dto.event;

import java.time.LocalDateTime;

public record FeaturedEventDto(Integer featuredId,
                              EventHomeDto event,
                              LocalDateTime startDate,
                               LocalDateTime endDate
) {
}