package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;

import java.time.LocalDateTime;

public record EventOrganiserDisplayDto(Integer organiserId,
                                       String name,
                                       String email,
                                       String description,
                                       String logoImage,
                                       String role,
                                       String reviewStatus,
                                       String reviewRemarks,
                                       AdminDisplayDto reviewedBy,
                                       Boolean enabled,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt,
                                       LocalDateTime deletedAt
) {
}

