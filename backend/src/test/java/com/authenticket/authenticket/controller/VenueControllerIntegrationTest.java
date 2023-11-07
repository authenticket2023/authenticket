package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EventOrganiserService;
import com.authenticket.authenticket.service.JwtService;
import com.authenticket.authenticket.service.VenueService;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import com.authenticket.authenticket.service.impl.VenueServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VenueControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private AmazonS3ServiceImpl amazonS3Service;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private VenueServiceImpl venueService;
    @Autowired
    JwtService jwtService;
    private static HttpHeaders headers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Venue venue;

    private Venue anotherVenue;
    private Admin admin;

    String token;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        Optional<Venue> oldVenue = venueRepository.findById(99);
        if(!oldVenue.isEmpty()){
            venueRepository.delete(oldVenue.get());
        }
        venue = Venue.builder()
                .venueId(99)
                .venueName("testPlace1")
                .venueLocation("testPlaceLocation1")
                .venueImage(null)
                .build();
        venueRepository.saveAndFlush(venue);
        anotherVenue = Venue.builder()
                .venueId(98)
                .venueName("testPlace2")
                .venueLocation("testPlaceLocation2")
                .venueImage(null)
                .build();


        String encodedPassword = new BCryptPasswordEncoder().encode("password");
        // ID is auto set to 1 when saving so changing this adminId is pointless
        admin = Admin.builder().adminId(1).password(encodedPassword).email("admin@gmail.com").name("admin").build();
        adminRepository.saveAndFlush(admin);
        token = jwtService.generateUserToken(admin);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @AfterEach
    void tearDown() {
        venueRepository.deleteAll();
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/v2/venue";
    }

    @Test
    public void testFindAllVenue() throws Exception {
        venueRepository.saveAndFlush(anotherVenue);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort(), HttpMethod.GET, entity, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("testPlace1");
    }

    @Test
    public void testFindVenueById() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/1", HttpMethod.GET, entity, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody()).contains("testPlace1");
    }

//    @Test
//    public void testSaveVenue() throws Exception {
//        // Create a custom Authentication object
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "admin@admin.com",  // username
//                "admin",   // password (if applicable)
//                List.of(new SimpleGrantedAuthority("ADMIN"))  // roles
//        );
//
//        // Set the custom authentication in the security context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        Venue savedVenue = new Venue();
//        given(venueRepository.findByVenueName(any())).willReturn(Optional.empty());
//        given(venueService.saveVenue(any())).willReturn(savedVenue);
//
//        MockMultipartFile imageFile = new MockMultipartFile("venueImage", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Some data".getBytes());
//        given(amazonS3Service.uploadFile(imageFile, "test.jpg", "jpg")).willReturn("ok");
//        mockMvc.perform(multipart("/api/v2/venue")
//                        .file(imageFile)
//                        .param("venueName", "Test Venue")
//                        .param("venueLocation", "Test Location")
//                        .param("venueDescription", "Test Description")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Venue created successfully"));
//    }

//    @Test
//    public void testUpdateVenue() throws Exception {
//        MockMultipartFile file
//                = new MockMultipartFile(
//                "file",
//                "hello.png",
//                MediaType.TEXT_PLAIN_VALUE,
//                "Hello, World!".getBytes()
//        );
//        Venue newVenue = Venue.builder()
//                .venueId(venue.getVenueId())
//                .venueName("New Venue Name")
//                .venueLocation("New Venue Location")
//                .venueImage(null)
//                .venueDescription("New Venue description")
//                .build();
//        venueRepository.saveAndFlush(newVenue);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("venueId", newVenue.getVenueId());
//        body.add("venueName", newVenue.getVenueName());
//        body.add("venueLocation", newVenue.getVenueLocation());
//        body.add("venueImage", file);
//        body.add("venueDescription", newVenue.getVenueDescription());
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort(), HttpMethod.PUT, entity, String.class);
//        System.out.println(responseEntity.getBody());
//        // Check if the response status code is 200 (OK) for a valid update
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        // Check if the response contains the updated admin's name
//        assertThat(responseEntity.getBody()).contains(newVenue.getVenueName());
//    }
}