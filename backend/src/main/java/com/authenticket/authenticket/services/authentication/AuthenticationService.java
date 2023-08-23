package com.authenticket.authenticket.services.authentication;

import com.authenticket.authenticket.controller.authentication.AuthenticationRequest;
import com.authenticket.authenticket.controller.authentication.AuthenticationResponse;
import com.authenticket.authenticket.controller.authentication.RegisterRequest;
import com.authenticket.authenticket.model.user.UserModel;
import com.authenticket.authenticket.repository.user.UserRepository;
import com.authenticket.authenticket.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    //Registration
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //Authentication
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserModel.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .date_of_birth(request.getDate_of_birth())
                //role of user to take note of
                .build();

        var existing = repository.findByEmail(request.getEmail())
                .isPresent();
        if(existing){
            throw new RuntimeException("User already exists");
        }

        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
