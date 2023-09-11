package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.AuthResponse.AuthenticationAdminResponse;
import com.authenticket.authenticket.controller.AuthResponse.AuthenticationOrgResponse;
import com.authenticket.authenticket.controller.AuthResponse.AuthenticationUserResponse;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;


public interface AuthenticationService {
    void userRegister(User request);
    AuthenticationUserResponse userAuthenticate(String email, String password);
    AuthenticationUserResponse confirmUserToken(String token);

    void orgRegister (EventOrganiser request);

    AuthenticationOrgResponse orgAuthenticate(String email, String password);

    AuthenticationAdminResponse adminAuthenticate(String email, String password);
}
