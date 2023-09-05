package com.authenticket.authenticket.controller.authentication;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private UserDisplayDto userDetails;
    private String role;
}
