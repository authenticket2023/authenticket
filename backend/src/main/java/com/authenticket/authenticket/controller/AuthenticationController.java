package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.exception.AwaitingVerificationException;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class AuthenticationController extends Utility{

    @Autowired
    private AuthenticationServiceImpl service;

    @PostMapping("/register")
    public ResponseEntity<GeneralApiResponse> register(
            @RequestBody User user
    ){
        service.register(user);
        return ResponseEntity.status(200).body(generateApiResponse(null, "Verification required"));
    }

    @GetMapping(path = "/register/confirm")
    public ResponseEntity<GeneralApiResponse> confirm(@RequestParam("token") String token){
        try{
            AuthenticationResponse response = service.confirmToken(token);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome"));
        } catch (AwaitingVerificationException | IllegalStateException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, e.getMessage()));
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<GeneralApiResponse> authenticate(
            @RequestBody User user
    ){
        try{
            AuthenticationResponse response = service.authenticate(user);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome"));
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(400).body(generateApiResponse(null,e.getMessage()));
        }
        catch (BadCredentialsException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Credentials are invalid."));
        }
        catch(LockedException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Please verify your account."));
        }
    }
}
