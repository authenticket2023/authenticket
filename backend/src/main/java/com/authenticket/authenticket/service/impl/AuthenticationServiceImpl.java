package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.controller.response.AuthenticationAdminResponse;
import com.authenticket.authenticket.controller.response.AuthenticationOrgResponse;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.controller.response.AuthenticationUserResponse;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.AwaitingVerificationException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.AuthenticationService;
import com.authenticket.authenticket.service.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation of the `AuthenticationService` interface, which handles user authentication, registration, and confirmation, as well as admin and event organiser authentication and registration.
 */

@Service
public class AuthenticationServiceImpl extends Utility implements AuthenticationService {

//    @Value("${authenticket.frontend-dev-url}")
//    private String frontendUrl;
//
//    @Value("${authenticket.backend-dev-url}")
//    private String backendUrl;

    @Value("${authenticket.loadbalancer-url}")
    private String frontendUrl;

    @Value("${authenticket.backend-production-url}")
    private String backendUrl;


    // All repos
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final EventOrganiserRepository organiserRepository;

    //JwtService
    private final JwtServiceImpl jwtServiceImpl;

    //Authentication
    private final AuthenticationManager authenticationManager;

    //Email Sender
    private final EmailServiceImpl emailServiceImpl;

    //UserDTO
    private final UserDtoMapper userDTOMapper;

    //EventOrgDTO
    private final EventOrganiserDtoMapper eventOrgDtoMapper;

    //AdminDTO
    private final AdminDtoMapper adminDtoMapper;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,
                                     AdminRepository adminRepository,
                                     EventOrganiserRepository organiserRepository,
                                     JwtServiceImpl jwtServiceImpl,
                                     AuthenticationManager authenticationManager,
                                     EmailServiceImpl emailServiceImpl,
                                     UserDtoMapper userDTOMapper,
                                     EventOrganiserDtoMapper eventOrgDtoMapper,
                                     AdminDtoMapper adminDtoMapper) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.organiserRepository = organiserRepository;
        this.jwtServiceImpl = jwtServiceImpl;
        this.authenticationManager = authenticationManager;
        this.emailServiceImpl = emailServiceImpl;
        this.userDTOMapper = userDTOMapper;
        this.eventOrgDtoMapper = eventOrgDtoMapper;
        this.adminDtoMapper = adminDtoMapper;
    }

    /**
     * Register a new user and send an email for confirmation.
     *
     * @param request The user registration request.
     * @throws AlreadyExistsException if the user already exists or is awaiting verification.
     */
    @Override
    public void userRegister(User request) {
        var existingUser = userRepository.findByEmail(request.getEmail());
        var existingAdmin = adminRepository.findByEmail(request.getEmail());
        var existingOrg = organiserRepository.findByEmail(request.getEmail());
        var jwtToken = jwtServiceImpl.generateToken(request);
        String redirectUrl = frontendUrl + "/Login";
        String link = backendUrl +"/api/v2/auth/user-register/confirm?token=" + jwtToken + "&redirect=" + redirectUrl;

        if(existingUser.isPresent() || existingAdmin.isPresent() || existingOrg.isPresent()){
            if(existingUser.isPresent() && !existingUser.get().getEnabled()){
                throw new AlreadyExistsException("Verification needed");
            }
            throw new AlreadyExistsException("User already exists");
        }

        userRepository.save(request);
        emailServiceImpl.send(request.getEmail(), EmailServiceImpl.buildActivationEmail(request.getName(), link), "Confirm your email");
    }

    /**
     * Authenticate a user using their email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return An authentication response containing a token and user details.
     * @throws UsernameNotFoundException if the user does not exist.
     */
    @Override
    public AuthenticationUserResponse userAuthenticate(String email, String password){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        var jwtToken = jwtServiceImpl.generateToken(user);

        return AuthenticationUserResponse.builder()
                .token(jwtToken)
                .userDetails(userDTOMapper.apply(user))
                .build();
    }

    /**
     * Confirm a user's registration using a confirmation token.
     *
     * @param token The confirmation token.
     * @return An authentication response containing a token and user details.
     * @throws AwaitingVerificationException if the token is expired or the email is already confirmed.
     */
    @Override
    public AuthenticationUserResponse confirmUserToken(String token) {
        if (jwtServiceImpl.isTokenExpired(token)) {
                throw new AwaitingVerificationException("Token expired");
        }

        String email = jwtServiceImpl.extractUsername(token);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        if (user.getEnabled()){
            throw new IllegalStateException("Email already confirmed");
        }

        userRepository.enableAppUser(email);

        var jwtToken = jwtServiceImpl.generateToken(user);
        return AuthenticationUserResponse.builder()
                .token(jwtToken)
                .userDetails(userDTOMapper.apply(user))
                .build();
    }

    /**
     * Register a new event organiser.
     *
     * @param request The event organiser registration request.
     * @throws AlreadyExistsException if the event organiser already exists or is awaiting approval.
     */
    @Override
    public void orgRegister (EventOrganiser request){

        var existingOrg = organiserRepository.findByEmail(request.getEmail());
        var existingUser = userRepository.findByEmail(request.getEmail());
        var existingAdmin = adminRepository.findByEmail(request.getEmail());

        if(existingUser.isPresent() || existingAdmin.isPresent() || existingOrg.isPresent()){
            if(existingOrg.isPresent() && !existingOrg.get().getEnabled()){
                throw new AlreadyExistsException("Awaiting approval");
            }
            throw new AlreadyExistsException("Organiser already exists");
        }
        emailServiceImpl.send(request.getEmail(), EmailServiceImpl.buildOrganiserPendingEmail(request.getName()), "Your account is under review");
        organiserRepository.save(request);
    }

    /**
     * Authenticate an event organiser using their email and password.
     *
     * @param email The event organiser's email.
     * @param password The event organiser's password.
     * @return An authentication response containing a token and event organiser details.
     * @throws UsernameNotFoundException if the event organiser does not exist.
     */
    @Override
    public AuthenticationOrgResponse orgAuthenticate(String email, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        var eventOrg = organiserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Event Organiser does not exist"));
        var jwtToken = jwtServiceImpl.generateToken(eventOrg);
        System.out.println(jwtToken);
        return AuthenticationOrgResponse.builder()
                .token(jwtToken)
                .orgDetails(eventOrgDtoMapper.apply(eventOrg))
                .build();
    }

    /**
     * Register a new admin.
     *
     * @param request The admin registration request.
     * @throws AlreadyExistsException if the admin already exists.
     */
    @Override
    public void adminRegister (Admin request){

        var existingAdmin = adminRepository.findByEmail(request.getEmail());
        var existingUser = userRepository.findByEmail(request.getEmail());
        var existingOrg = organiserRepository.findByEmail(request.getEmail());

        if(existingUser.isPresent() || existingAdmin.isPresent() || existingOrg.isPresent()){
            throw new AlreadyExistsException("Admin already exists");
        }
        adminRepository.save(request);
    }

    /**
     * Authenticate an admin using their email and password.
     *
     * @param email The admin's email.
     * @param password The admin's password.
     * @return An authentication response containing a token and admin details.
     * @throws UsernameNotFoundException if the admin does not exist.
     */
    @Override
    public AuthenticationAdminResponse adminAuthenticate(String email, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        var admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin does not exist"));
        var jwtToken = jwtServiceImpl.generateToken(admin);

        return AuthenticationAdminResponse.builder()
                .token(jwtToken)
                .adminDetails(adminDtoMapper.apply(admin))
                .build();
    }
}
