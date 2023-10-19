package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.AuthenticationAdminResponse;
import com.authenticket.authenticket.controller.response.AuthenticationOrgResponse;
import com.authenticket.authenticket.controller.response.AuthenticationUserResponse;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.exception.AwaitingVerificationException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AuthenticationServiceImpl;
import com.authenticket.authenticket.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.security.SignatureException;

import java.net.URI;
import java.time.LocalDate;

/**This is the authentication controller class and the base path for this controller's endpoint is api/v2/auth.*/

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/v2/auth")
public class AuthenticationController extends Utility{

    private final AuthenticationServiceImpl service;

    private final PasswordEncoder passwordEncoder;

    private final JwtServiceImpl jwtService;

    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationController(AuthenticationServiceImpl service,
                                    PasswordEncoder passwordEncoder,
                                    JwtServiceImpl jwtService,
                                    UserDetailsService userDetailsService) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Verify the validity of a JWT token and check if it is associated with a specific user.
     *
     * This endpoint is mapped to an HTTP POST request and is used to verify the validity of a JWT token
     * and check if the token is associated with a specific user identified by their email.
     *
     * @param token The JWT token to be verified.
     * @param userEmail The email of the user to which the token should be associated.
     * @return A ResponseEntity with a GeneralApiResponse indicating the result of token verification. If the token is valid
     *         and associated with the provided user, it returns a success message along with user authorities. If the token
     *         is invalid, it returns an error message. If the provided email is not registered, it returns an error message.
     *         If the token's signature cannot be trusted, it returns an error message.
     */

    @PostMapping("token-verification")
    public ResponseEntity<GeneralApiResponse<Object>> tokenCheck(
            @RequestParam("jwtToken") String token,
            @RequestParam("userEmail") String userEmail){
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            boolean jwtValidity = jwtService.isTokenValid(token, userDetails);
            if (jwtValidity) {
                return ResponseEntity.status(200).body(generateApiResponse(true, "valid token." + userDetails.getAuthorities()));
            }
            return ResponseEntity.status(400).body(generateApiResponse(false, "invalid token"));
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(400).body(generateApiResponse(null, "email not registered"));
        } catch (SignatureException e) {
            return ResponseEntity.status(400).body(generateApiResponse(false, "invalid token, token should not be trusted"));
        }
    }

    /**
     * Register a new user with the provided information.
     *
     * This endpoint is mapped to an HTTP POST request and is used to register a new user with the provided information,
     * including their name, email, password, and date of birth.
     *
     * @param name The name of the user to be registered.
     * @param email The email address of the user to be registered.
     * @param password The password for the user's account (will be securely hashed and stored).
     * @param dob The date of birth of the user.
     * @return A ResponseEntity with a GeneralApiResponse indicating the success of user registration. The response
     *         typically includes a message stating that user verification is required.
     */

    @PostMapping("/user-register")
    public ResponseEntity<GeneralApiResponse<Object>> userRegister(
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

    /**
     * Confirm a user's registration using a verification token and redirect to a specified URL. Used in email when a confirmation email is sent to the user's email to verify the user.
     *
     * This endpoint is mapped to an HTTP GET request and is used to confirm a user's registration by providing a verification token.
     * After successful confirmation, the endpoint redirects to the specified URL.
     *
     * @param token The verification token used to confirm the user's registration.
     * @param redirect The URL to which the endpoint should redirect after confirmation.
     * @return A ResponseEntity that typically performs a redirect response to the specified URL if confirmation is successful,
     *         or returns an error response with a message if confirmation fails.
     */
    @GetMapping(path = "/user-register/confirm")
    public ResponseEntity<GeneralApiResponse<Object>> userConfirm(@RequestParam("token") String token,
                                                          @RequestParam("redirect") String redirect){
        try{
            AuthenticationUserResponse response = service.confirmUserToken(token);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirect))
                    .build();
        } catch (AwaitingVerificationException | IllegalStateException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, e.getMessage()));
        }
    }

    /**
     * Authenticate a user by their email and password.
     *
     * This endpoint is mapped to an HTTP POST request and is used to authenticate a user by providing their email and password.
     * If authentication is successful, it returns a success message with the user's email. If the email is not registered, it returns an error message.
     * If the account is locked (e.g., pending verification), it returns an error message indicating the need for verification.
     *
     * @param email The email of the user to authenticate.
     * @param password The password of the user for authentication.
     * @return A ResponseEntity with a GeneralApiResponse indicating the result of user authentication. If successful, it returns a welcome message,
     *         or an error message if authentication fails due to an unregistered email or account lock.
     */
    @PostMapping("/user")
    public ResponseEntity<GeneralApiResponse<Object>> userAuthenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){

        try{
            AuthenticationUserResponse response = service.userAuthenticate(email, password);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome " + email));
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(400).body(generateApiResponse(null,"email not registered"));
        }
        catch(LockedException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Please verify your account."));
        }
    }

    /**
     * Register a new organization (event organizer) with the provided information.
     *
     * @param name The name of the organization to be registered.
     * @param email The email address of the organization.
     * @param description A description of the organization.
     * @return A ResponseEntity indicating the success of organization registration.
     */

    @PostMapping("/org-register")
    public ResponseEntity<GeneralApiResponse<Object>> orgRegister(
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
                .reviewStatus("pending")
                .build();

        service.orgRegister(eventOrg);
        return ResponseEntity.status(200).body(generateApiResponse(null, "Approval required"));
    }

    /**
     * Authenticate an organization (event organizer) by their email and password.
     *
     * @param email The email of the organization.
     * @param password The password for authentication.
     * @return A ResponseEntity indicating the result of organization authentication.
     */

    @PostMapping("/org")
    public ResponseEntity<GeneralApiResponse<Object>> eventOrgAuthenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        try{
            AuthenticationOrgResponse response = service.orgAuthenticate(email, password);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome " + email));
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(400).body(generateApiResponse(null,"email not registered"));
        }
        catch(LockedException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Account not yet approved by company administrator"));
        }
    }


    /**
     * Register a new admin with the provided information.
     *
     * This endpoint is mapped to an HTTP POST request and is used to register a new admin with the provided information,
     * including their name, email, and password.
     *
     * @param name The name of the admin to be registered.
     * @param email The email address of the admin.
     * @param password The password for the admin's account (will be securely hashed and stored).
     * @return A ResponseEntity indicating the success of admin registration.
     */

    @PostMapping("/admin-register")
    public ResponseEntity<GeneralApiResponse<Object>> adminRegister(
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

    /**
     * Authenticate an admin by their email and password.
     *
     * This endpoint is mapped to an HTTP POST request and is used to authenticate an admin by providing their email and password.
     * If authentication is successful, it returns a success message with the admin's email. If the email is not registered, it returns an error message.
     * If the account is locked (e.g., pending verification), it returns an error message indicating the need for verification.
     *
     * @param email The email of the admin to authenticate.
     * @param password The password of the admin for authentication.
     * @return A ResponseEntity indicating the result of admin authentication. If successful, it returns a welcome message,
     *         or an error message if authentication fails due to an unregistered email or account lock.
     */

    @PostMapping("/admin")
    public ResponseEntity<GeneralApiResponse<Object>> adminAuthenticate(
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        try{

            AuthenticationAdminResponse response = service.adminAuthenticate(email, password);
            return ResponseEntity.status(200).body(generateApiResponse(response, "Welcome " + email));
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(400).body(generateApiResponse(null,"email not registered"));
        }
        catch(LockedException e){
            return ResponseEntity.status(400).body(generateApiResponse(null, "Please verify your account."));
        }
    }
}
