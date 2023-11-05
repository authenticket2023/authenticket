package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EventOrganiserService;
import com.authenticket.authenticket.service.VenueService;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import com.authenticket.authenticket.service.impl.VenueServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VenueController.class)
@ExtendWith(SpringExtension.class)
@ComponentScan("com.authenticket.authenticket")
public class VenueControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private VenueRepository venueRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AdminRepository adminRepository;
    @MockBean
    private EventOrganiserRepository eventOrganiserRepository;
    @MockBean
    private EventOrganiserServiceImpl eventOrganiserService;
    @MockBean
    private EventServiceImpl eventService;
    @MockBean
    private AmazonS3ServiceImpl amazonS3Service;
    @MockBean
    private EventTypeRepository eventTypeRepository;
    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private ArtistRepository artistRepository;
    @MockBean
    private JavaMailSenderImpl javaMailSender;
    @MockBean
    private TicketRepository ticketRepository;
    @MockBean
    private PresaleInterestRepository presaleInterestRepository;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private TicketPricingRepository ticketPricingRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private QueueRepository queueRepository;
    @MockBean
    private TicketCategoryRepository ticketCategoryRepository;
    @MockBean
    private VenueServiceImpl venueService;
    @BeforeEach
    public void setUp() {
        // Define your mock behavior here using given(...) and when(...)
    }

    @Test
    public void testFindAllVenue() throws Exception {
        Venue venue1 = Venue.builder().venueId(1).build();
        Venue venue2 = Venue.builder().venueId(2).build();
        given(venueService.findAllVenue()).willReturn(List.of(venue1, venue2));

        mockMvc.perform(get("/api/v2/venue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Venue successfully returned."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].venueId").exists());
    }

    @Test
    public void testFindVenueById() throws Exception {

        int venueId = 1;
        Venue venue = Venue.builder().venueId(1).build();
        given(venueService.findById(venueId)).willReturn(Optional.of(venue));

        mockMvc.perform(get("/api/v2/venue/" + venueId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.venueId").exists());
    }

    @Test
    public void testSaveVenue() throws Exception {
        // Create a custom Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@admin.com",  // username
                "admin",   // password (if applicable)
                List.of(new SimpleGrantedAuthority("ADMIN"))  // roles
        );

        // Set the custom authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Venue savedVenue = new Venue();
        given(venueRepository.findByVenueName(any())).willReturn(Optional.empty());
        given(venueService.saveVenue(any())).willReturn(savedVenue);

        MockMultipartFile imageFile = new MockMultipartFile("venueImage", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Some data".getBytes());

        mockMvc.perform(multipart("/api/v2/venue")
                        .file(imageFile)
                        .param("venueName", "Test Venue")
                        .param("venueLocation", "Test Location")
                        .param("venueDescription", "Test Description")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Venue created successfully"));
    }

    @Test
    public void testUpdateVenue() throws Exception {
        // Create a custom Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "",  // username
                "",   // password (if applicable)
                List.of(new SimpleGrantedAuthority("ADMIN"))  // roles
        );

        // Set the custom authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Venue updatedVenue = Venue.builder().venueId(1).build();
        given(venueService.updateVenue(any(), any(), any())).willReturn(updatedVenue);

        MockMultipartFile imageFile = new MockMultipartFile("venueImage", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Some data".getBytes());

        mockMvc.perform(multipart("/api/v2/venue")
                        .file(imageFile)
                        .param("venueId", "1")
                        .param("venueName", "Updated Venue Name")
                        .param("venueLocation", "Updated Venue Location")
                        .param("venueDescription", "Updated Venue Description")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Venue updated successfully"));
    }
}