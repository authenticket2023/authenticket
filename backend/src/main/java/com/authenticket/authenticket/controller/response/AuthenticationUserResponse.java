package com.authenticket.authenticket.controller.response;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationUserResponse {
    private String token;
    private UserDisplayDto userDetails;
}
