package com.authenticket.authenticket.controller.authentication;

import com.authenticket.authenticket.model.user.User;
import com.authenticket.authenticket.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User user
    ){

        return service.register(user);
    }

    @GetMapping(path = "/register/confirm")
    public ResponseEntity<AuthenticationResponse> confirm(@RequestParam("token") String token){
        return service.confirmToken(token);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody User user
    ){
        return service.authenticate(user);
    }
}
