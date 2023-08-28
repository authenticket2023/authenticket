package com.authenticket.authenticket.dto.user;

import java.time.LocalDate;

public record UserDto(String name,
                      String email,
                      LocalDate date_of_birth,
                      String profile_image){
}
