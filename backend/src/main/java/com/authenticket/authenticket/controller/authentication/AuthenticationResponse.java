package com.authenticket.authenticket.controller.authentication;

import com.authenticket.authenticket.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String message;
    private String token;
    private UserDto userDetails;
}
