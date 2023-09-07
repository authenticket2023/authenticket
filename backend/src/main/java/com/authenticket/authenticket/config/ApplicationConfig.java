package com.authenticket.authenticket.config;

import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.Optional;

@Configuration
public class ApplicationConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            Optional<Admin> adminOptional = adminRepository.findByEmail(username);
            Optional<com.authenticket.authenticket.model.User> userOptional = userRepository.findByEmail(username);
            Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findByEmail(username);
            if (adminOptional.isPresent()) {
                return new User(adminOptional.get().getEmail(), adminOptional.get().getPassword(), Collections.emptyList());
            } else if (userOptional.isPresent()){
                return new User(userOptional.get().getEmail(), userOptional.get().getPassword(), Collections.emptyList());
            } else if(eventOrganiserOptional.isPresent()){
                return new User(eventOrganiserOptional.get().getEmail(), eventOrganiserOptional.get().getPassword(), Collections.emptyList());
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        };
    }


    @Bean
    public AuthenticationProvider userAuthenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
