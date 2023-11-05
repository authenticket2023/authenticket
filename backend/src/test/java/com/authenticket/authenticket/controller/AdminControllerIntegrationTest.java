package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.ConfigProperties;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.JwtService;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.authenticket.authenticket.model.Admin;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminController.class)
@ComponentScan("com.authenticket.authenticket")
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

//    @Autowired
//    private JwtService jwtService;
    @MockBean
    private AdminRepository adminRepository;

    String jwtToken;
    @BeforeEach
    void init(){
//        given(jwtService.generateUserToken(any())).willReturn("");
//        jwtToken = jwtService.generateUserToken(User.builder().userId(1).build());
    }

    @Test
    public void testFindAllAdmin() throws Exception {
        // Create a custom Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@admin.com",  // username
                "admin",   // password (if applicable)
                List.of(new SimpleGrantedAuthority("ADMIN"))  // roles
        );

        // Set the custom authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ArrayList<Admin> list = new ArrayList<>();
        list.add(new Admin());
        list.add(new Admin());
        given(adminRepository.findAll()).willReturn(list);
        mockMvc.perform(get("/api/v2/admin").contentType("application/json")).andExpect(jsonPath("$").isArray()).andExpect(status().isOk());
    }

    @Test
    public void testFindAllAdmin_Failure() throws Exception {
        mockMvc.perform(get("/api/v2/admin")).andExpect(status().is4xxClientError());
    }

    @Test
    public void testFindAdminById() throws Exception {
        Admin admin = Admin.builder().adminId(1).build();
        // Create a custom Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@admin.com",  // username
                "admin",   // password (if applicable)
                List.of(new SimpleGrantedAuthority("ADMIN"))  // roles
        );

        // Set the custom authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(adminRepository.findById(1)).willReturn(Optional.of(admin));
        mockMvc.perform(get("/api/v2/admin/1").contentType("application/json")).andExpect(jsonPath("$.data.adminId").value(1)).andExpect(status().isOk());
    }

    @Test
    public void testFindAdminById_Failure() throws Exception {
        mockMvc.perform(get("/api/v2/admin/1")).andExpect(status().is4xxClientError());
    }

    @Test
    public void testUpdateAdmin() throws Exception {
        // Create a custom Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@admin.com",  // username
                "admin",   // password (if applicable)
                List.of(new SimpleGrantedAuthority("ADMIN"))  // roles
        );

        // Set the custom authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create a new Admin object to send in the request
        Admin adminToUpdate = Admin.builder().adminId(1).email("newemail@example.com").name("newname").password("test").build();

        given(adminRepository.findByEmail(any())).willReturn(Optional.of(adminToUpdate));
        mockMvc.perform(put("/api/v2/admin").content("{\"adminId\": \"1\",\n" +
                "    \"name\": \"test12\",\n" +
                "    \"password\": \"password12345\",\n" +
                "    \"email\": \"test123456\"}").contentType("application/json")).andExpect(jsonPath("$.data.adminId").value(1)).andExpect(status().isOk());
    }

    @Test
    public void testUpdateAdmin_Failure() throws Exception {
        mockMvc.perform(put("/api/v2/admin")).andExpect(status().is4xxClientError());
    }
}
