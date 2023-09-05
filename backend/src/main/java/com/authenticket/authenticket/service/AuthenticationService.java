package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.model.User;
import org.springframework.http.ResponseEntity;


public interface AuthenticationService {
    ResponseEntity<AuthenticationResponse> register(User request);
    ResponseEntity<AuthenticationResponse> authenticate(User request);
    ResponseEntity<AuthenticationResponse> confirmToken(String token);
}
