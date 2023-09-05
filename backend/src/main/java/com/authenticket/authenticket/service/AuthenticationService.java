package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.model.User;
import org.springframework.http.ResponseEntity;


public interface AuthenticationService {
    void register(User request);
    AuthenticationResponse authenticate(User request);
    AuthenticationResponse confirmToken(String token);
}
