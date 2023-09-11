package com.authenticket.authenticket.dto.eventOrganiser;

import java.time.LocalDateTime;

public record EventOrganiserDisplayDto(Integer organiserId,
                                       String name,
                                       String email,
                                       String description,
                                       Integer verifiedBy,
                                       String logoImage,
                                       String role
) {
}

