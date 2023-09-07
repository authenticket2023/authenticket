package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import org.springframework.http.ResponseEntity;


public interface AuthenticationService {
    void userRegister(User request);
    AuthenticationResponse userAuthenticate(User request);
    AuthenticationResponse confirmUserToken(String token);

    void orgRegister (EventOrganiser request);

    AuthenticationResponse orgAuthenticate(EventOrganiser request);

    AuthenticationResponse adminAuthenticate(Admin request);
}
