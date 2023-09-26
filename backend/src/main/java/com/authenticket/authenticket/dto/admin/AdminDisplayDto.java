package com.authenticket.authenticket.dto.admin;

import java.time.LocalDateTime;

public record AdminDisplayDto(Integer adminId,
                              String name,
                              String email,
                              String role,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              LocalDateTime deletedAt
) {
}
