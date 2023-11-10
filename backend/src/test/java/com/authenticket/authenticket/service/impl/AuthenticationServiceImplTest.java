package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.controller.response.AuthenticationOrgResponse;
import com.authenticket.authenticket.controller.response.AuthenticationUserResponse;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private AuthenticationServiceImpl underTest;

    @Mock
    private JavaMailSenderImpl mailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EventOrganiserRepository organiserRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private AdminDtoMapper adminDtoMapper;

    @InjectMocks
    private EventOrganiserDtoMapper eventOrgDtoMapper;

    @BeforeEach
    void setUp(){
        JwtServiceImpl jwtServiceImpl = new JwtServiceImpl();
        EmailServiceImpl emailServiceImpl = new EmailServiceImpl(mailSender);
        underTest = new AuthenticationServiceImpl(
                userRepository,
                adminRepository,
                organiserRepository,
                jwtServiceImpl,
                authenticationManager,
                emailServiceImpl,
                userDtoMapper,
                eventOrgDtoMapper,
                adminDtoMapper
        );

    }
    //anything that requires the jwt service cannot be tested as secret key cannot be parsed it
    //anything that requires email service cannot be replicate either
    @Test
    public void testUserRegisterUserExists() {
        // Mock data
        String email = "test@example.com";
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email(email)
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();

        // Mock repository behavior to simulate an existing user
        userRepository.save(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Test the method and expect an exception
        //IllegalArgumentException thrown due to limitations in jwtServiceImpl and its secret
        assertThrows(IllegalArgumentException.class, () -> underTest.userRegister(user));

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void testOrgRegisterOrganiserExists() {
        // Mock data
        List<Event> eventList = new ArrayList<>();
        EventOrganiser eventOrg = EventOrganiser
                .builder()
                .organiserId(99)
                .name("TestGeorgia")
                .email("testOrg@example.com")
                .password("password")
                .description("description")
                .logoImage(null)
                .enabled(true)
                .admin(null)
                .reviewStatus("status")
                .reviewRemarks("remarks")
                .events(eventList)
                .build();

        // Mock repository behavior to simulate an existing event organiser
        organiserRepository.save(eventOrg);
        when(organiserRepository.findByEmail(eventOrg.getEmail())).thenReturn(Optional.of(eventOrg));

        // Test the method and expect an exception
        assertThrows(AlreadyExistsException.class, () -> underTest.orgRegister(eventOrg));

        // Verify interactions
        verify(organiserRepository, times(1)).findByEmail(eventOrg.getEmail());
    }

    @Test
    public void testAdminRegister() {
        // Mock data
        Integer adminId = 99;
        String email = "test@example.com";
        Admin admin = Admin.builder()
                .adminId(adminId)
                .name("UpdatedGeorgia")
                .email(email)
                .password("password12")
                .eventOrganiser(null)
                .build();

        // Mock repository behavior
        when(adminRepository.findByEmail(admin.getEmail())).thenReturn(Optional.empty());

        // Test the method
        underTest.adminRegister(admin);

        // Verify interactions
        verify(adminRepository).save(admin);
    }

    @Test
    public void testAdminRegisterUserExists() {
        // Mock data
        Integer adminId = 99;
        String email = "test@example.com";
        Admin admin = Admin.builder()
                .adminId(adminId)
                .name("UpdatedGeorgia")
                .email(email)
                .password("password12")
                .eventOrganiser(null)
                .build();

        // Mock repository behavior to simulate an existing user
        adminRepository.save(admin);
        when(adminRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        // Test the method and expect an exception
        assertThrows(AlreadyExistsException.class, () -> underTest.adminRegister(admin));

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(admin.getEmail());
    }
}