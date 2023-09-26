package com.authenticket.authenticket.dto.user;

import java.time.LocalDate;

public record UserDisplayDto(Integer userId,
                             String name,
                             String email,
                             LocalDate dateOfBirth,
                             String profileImage,
                             String role){
}
