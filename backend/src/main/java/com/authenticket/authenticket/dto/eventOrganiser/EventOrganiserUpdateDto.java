package com.authenticket.authenticket.dto.eventOrganiser;

import java.time.LocalDateTime;

public record EventOrganiserUpdateDto(Integer organiserId,
                                       String name,
                                       String description,
                                       String password,
                                       Boolean enabled
) {
}
