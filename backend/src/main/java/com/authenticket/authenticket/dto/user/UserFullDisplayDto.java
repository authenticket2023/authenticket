package com.authenticket.authenticket.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserFullDisplayDto(Integer userId,
                                 String name,
                                 String email,
                                 LocalDate dateOfBirth,
                                 String profileImage,
                                 String role,
                                 Boolean enabled,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt,
                                 LocalDateTime deletedAt){
}
