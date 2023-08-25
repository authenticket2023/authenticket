package com.authenticket.authenticket.DTO.user;

import java.time.LocalDate;

public record UserDTO (String name,
                       String email,
                       LocalDate date_of_birth,
                       String profile_image){
}
