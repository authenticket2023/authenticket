package com.authenticket.authenticket.controller.response;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationAdminResponse {
    private String token;
    private AdminDisplayDto adminDetails;
}
