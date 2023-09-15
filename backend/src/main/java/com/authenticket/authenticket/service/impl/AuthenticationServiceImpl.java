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

@Service
public class AuthenticationServiceImpl extends Utility implements AuthenticationService {

    @Value("${authenticket.api-port}")
    private String apiPort;

    // User repos
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EventOrganiserRepository organiserRepository;

    //JwtService
    @Autowired
    private JwtServiceImpl jwtServiceImpl;

    //Authentication
    @Autowired
    private AuthenticationManager authenticationManager;

    //Email Sender
    @Autowired
    private EmailServiceImpl emailServiceImpl;

    //UserDTO
    @Autowired
    private UserDtoMapper userDTOMapper;

    //EventOrgDTO
    @Autowired
    private EventOrganiserDtoMapper eventOrgDtoMapper;

    //AdminDTO
    @Autowired
    private AdminDtoMapper adminDtoMapper;

    //user
    public void userRegister(User request) {
        var existingUser = userRepository.findByEmail(request.getEmail());

        if(existingUser.isPresent()){
            if(!existingUser.get().getEnabled()){
                throw new AlreadyExistsException("Verification needed");
            }
            throw new AlreadyExistsException("User already exists");
        }

        userRepository.save(request);
        var jwtToken = jwtServiceImpl.generateToken(request);

        String link = "http://localhost:" + apiPort + "/api/auth/register/confirm?token=" + jwtToken;
        emailServiceImpl.send(request.getEmail(), EmailServiceImpl.buildActivationEmail(request.getName(), link), "Confirm your email");
    }

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

    public void orgRegister (EventOrganiser request){

        var existingOrg = organiserRepository.findByEmail(request.getEmail());

        if(existingOrg.isPresent()){
            if(!existingOrg.get().getEnabled()){
                throw new AlreadyExistsException("Awaiting approval");
            }
            throw new AlreadyExistsException("User already exists");
        }
        emailServiceImpl.send(request.getEmail(), EmailServiceImpl.buildOrganiserPendingEmail(request.getName()), "Your account is under review");
        organiserRepository.save(request);
    }

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

    public void adminRegister (Admin request){

        var existingAdmin = adminRepository.findByEmail(request.getEmail());

        if(existingAdmin.isPresent()){
            throw new AlreadyExistsException("Admin already exists");
        }
        adminRepository.save(request);
    }

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
