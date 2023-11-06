package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.AdminService;
import com.authenticket.authenticket.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.authenticket.authenticket.model.Admin;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AdminService adminService;

    @Autowired
    EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    JwtService jwtService;

    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Admin admin;
    private Admin anotherAdmin;

    String token;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        Optional<Admin> oldAdmin = adminRepository.findByEmail("admin@gmail.com");
        if (!oldAdmin.isEmpty()) {
            adminRepository.delete(oldAdmin.get());
        }
        String encodedPassword = new BCryptPasswordEncoder().encode("password");
        // ID is auto set to 1 when saving so changing this adminId is pointless
        admin = Admin.builder().adminId(1).password(encodedPassword).email("admin@gmail.com").name("admin").build();
        adminRepository.saveAndFlush(admin);
        // ID is auto set to 2 when saving so changing this adminId is pointless
        anotherAdmin = Admin.builder().adminId(2).password(encodedPassword).email("admin2@gmail.com").name("admin2").build();

        token = jwtService.generateUserToken(admin);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/v2/admin";
    }

    @Test
    public void testFindAllAdmin() {
        adminRepository.saveAndFlush(anotherAdmin);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort(), HttpMethod.GET, entity, String.class);

        // Check if the response status code is 200 (OK)
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).contains("admin@gmail.com");
        assertThat(responseEntity.getBody()).contains("admin2@gmail.com");
    }

    @Test
    public void testFindAdminById() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        // Send a GET request to the "/api/v2/admin/{admin_id}" endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/1", HttpMethod.GET, entity, String.class);
        // Check if the response status code is 200 (OK) for a valid admin ID
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // You can also check the response body or other aspects of the response
        assertThat(responseEntity.getBody()).contains("admin@gmail.com");
    }

    @Test
    public void testUpdateAdmin() throws JsonProcessingException {
        admin.setName("UpdatedAdminName");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(admin), headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort(), HttpMethod.PUT, entity, String.class);
        System.out.println(responseEntity.getBody());
        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Check if the response contains the updated admin's name
        assertThat(responseEntity.getBody()).contains("UpdatedAdminName");
    }

    @Test
    public void testUpdateEventOrganiser() throws JsonProcessingException {
        // Implement a test for the 'update-organiser' endpoint
        // Send a PUT request with the necessary parameters and check the response

        // Example:
        // - Create a new EventOrganiserUpdateDto with some changes
        // - Send a PUT request with the changes to the 'update-organiser' endpoint
        // - Check if the response status code is as expected (e.g., HttpStatus.OK)
        // - Check if the response body contains the expected data
        EventOrganiser organiser = new EventOrganiser();
        organiser.setDescription("desc");
        organiser.setPassword("bla");
        organiser.setEmail("e");
        organiser.setName("E");
        eventOrganiserRepository.saveAndFlush(organiser);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("organiserId", "1");
        body.add("name", "new name");

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort() + "/update-organiser?organiserId=1&name=new name", HttpMethod.PUT, entity, String.class);
        System.out.println(responseEntity.getBody());
        // Check if the response status code is 200 (OK) for a valid update
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Check if the response contains the updated admin's name
        assertThat(responseEntity.getBody()).contains("new name");
    }

    @Test
    public void testUpdateEvent() {
        // Implement a test for the 'update-event' endpoint
        // Send a PUT request with the necessary parameters and check the response

        // Example:
        // - Create a new EventUpdateDto with some changes
        // - Send a PUT request with the changes to the 'update-event' endpoint
        // - Check if the response status code is as expected (e.g., HttpStatus.OK)
        // - Check if the response body contains the expected data

        // You can use a similar approach as in the 'testUpdateAdmin' method.
    }

    @Test
    public void testFindEventOrganisersByReviewStatus() {
        // Implement a test for the 'event-organiser/review-status/{status}' endpoint
        // Send a GET request with a valid status (e.g., "approved") and check the response

        // Example:
        // - Send a GET request to the 'event-organiser/review-status/approved' endpoint
        // - Check if the response status code is as expected (e.g., HttpStatus.OK)
        // - Check if the response body contains data for approved event organizers

        // You can use a similar approach as in the 'testFindAllAdmin' method.
    }

    @Test
    public void testFindEventsByReviewStatus() {
        // Implement a test for the 'event/review-status/{status}' endpoint
        // Send a GET request with a valid status (e.g., "approved") and check the response

        // Example:
        // - Send a GET request to the 'event/review-status/approved' endpoint
        // - Check if the response status code is as expected (e.g., HttpStatus.OK)
        // - Check if the response body contains data for approved events

        // You can use a similar approach as in the 'testFindAllAdmin' method.
    }
}
