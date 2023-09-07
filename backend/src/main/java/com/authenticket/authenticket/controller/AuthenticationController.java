package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.AuthResponse.AuthenticationAdminResponse;
import com.authenticket.authenticket.controller.AuthResponse.AuthenticationOrgResponse;
import com.authenticket.authenticket.controller.AuthResponse.AuthenticationUserResponse;
import com.authenticket.authenticket.exception.AwaitingVerificationException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class AuthenticationController extends Utility{

    @Autowired
    private AuthenticationServiceImpl service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/userRegister")
    public ResponseEntity<GeneralApiResponse> userRegister(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("dateOfBirth") LocalDate dob
    ){
        var user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .dateOfBirth(dob)
                .enabled(false)
                .build();
        service.userRegister(user);
        return ResponseEntity.status(200).body(generateApiResponse(null, "Verification required"));
    }

    @GetMapping(path = "/userRegister/userConfirm")
    public ResponseEntity<GeneralApiResponse> userConfirm(@RequestParam("token") String token){
        try{
            AuthenticationUserResponse response = service.confirmUserToken(token);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome"));
        } catch (AwaitingVerificationException | IllegalStateException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, e.getMessage()));
        }
    }

    @PostMapping("/userAuthenticate")
    public ResponseEntity<GeneralApiResponse> userAuthenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){

        try{
            AuthenticationUserResponse response = service.userAuthenticate(email, password);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome " + email));
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

    @PostMapping("/orgRegister")
    public ResponseEntity<GeneralApiResponse> orgRegister(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("description") String description
    ){
        var eventOrg = EventOrganiser.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(generateRandomPassword()))
                .description(description)
                .enabled(false)
                .build();

        service.orgRegister(eventOrg);
        return ResponseEntity.status(200).body(generateApiResponse(null, "Approval required"));
    }

    @PostMapping("/eventOrgAuthenticate")
    public ResponseEntity<GeneralApiResponse> eventOrgAuthenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        try{
            AuthenticationOrgResponse response = service.orgAuthenticate(email, password);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome " + email));
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(400).body(generateApiResponse(null,e.getMessage()));
        }
        catch (BadCredentialsException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Credentials are invalid."));
        }
        catch(LockedException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Account not yet approved by company administrator"));
        }
    }

    @PostMapping("/adminRegister")
    public ResponseEntity<GeneralApiResponse> adminRegister(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        var admin = Admin.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        service.adminRegister(admin);
        return ResponseEntity.status(200).body(generateApiResponse(null, "Admin account created"));
    }

    @PostMapping("/adminAuthenticate")
    public ResponseEntity<GeneralApiResponse> adminAuthenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        try{

            AuthenticationAdminResponse response = service.adminAuthenticate(email, password);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome " + email));
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
