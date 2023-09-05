package com.authenticket.authenticket.dto.eventOrganiser;

import java.time.LocalDateTime;

public record EventOrganiserUpdateDto(Integer organiserId,
                                       String description
) {
}
