package com.authenticket.authenticket.controller.response;

import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationOrgResponse {
    private String token;
    private EventOrganiserDisplayDto orgDetails;
}
