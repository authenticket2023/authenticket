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
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EventOrganiserRepository organiserRepository;

    @InjectMocks
    private JwtServiceImpl jwtServiceImpl;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        UserDtoMapper userDtoMapper = new UserDtoMapper(passwordEncoder);
        AdminDtoMapper adminDtoMapper = new AdminDtoMapper(passwordEncoder);
        EventOrganiserDtoMapper eventOrgDtoMapper = new EventOrganiserDtoMapper(passwordEncoder, adminDtoMapper);
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
        when(organiserRepository.findByEmail(eventOrg.getEmail())).thenReturn(Optional.of(eventOrg));
        organiserRepository.save(eventOrg);
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
        when(adminRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        adminRepository.save(admin);
        // Test the method and expect an exception
        assertThrows(AlreadyExistsException.class, () -> underTest.adminRegister(admin));

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(admin.getEmail());
    }
}