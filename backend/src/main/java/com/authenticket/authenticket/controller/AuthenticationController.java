package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.GeneralApiResponse;
import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.AwaitingVerificationException;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        try{
            service.register(user);
        }  catch(AlreadyExistsException e){
            return ResponseEntity.status(400).body(generateApiResponse(null,e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(400).body(generateApiResponse(null, e.getMessage()));
        }

        return ResponseEntity.status(200).body(generateApiResponse(null, "Verification required"));
    }

    @GetMapping(path = "/register/confirm")
    public ResponseEntity<GeneralApiResponse> confirm(@RequestParam("token") String token){
        try{
            AuthenticationResponse response = service.confirmToken(token);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome"));
        } catch (AwaitingVerificationException | IllegalStateException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Something went wrong" + e.getMessage()));
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
        } catch (Exception e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Something went wrong" + e.getMessage()));
        }
    }
}
