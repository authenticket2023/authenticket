package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.response.AuthenticationAdminResponse;
import com.authenticket.authenticket.controller.response.AuthenticationOrgResponse;
import com.authenticket.authenticket.controller.response.AuthenticationUserResponse;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;

public interface AuthenticationService {
    void userRegister(User request);
    AuthenticationUserResponse userAuthenticate(String email, String password);
    AuthenticationUserResponse confirmUserToken(String token);

    void orgRegister (EventOrganiser request);

    AuthenticationOrgResponse orgAuthenticate(String email, String password);

    void adminRegister (Admin request);

    AuthenticationAdminResponse adminAuthenticate(String email, String password);
}
