package com.authenticket.authenticket.config;

import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Profile("test")
public class TestProfileConfig {
    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
    @Bean
    public AdminRepository adminRepository() {
        return Mockito.mock(AdminRepository.class);
    }
    @Bean
    public EventOrganiserRepository eventOrganiserRepository() {
        return Mockito.mock(EventOrganiserRepository.class);
    }
    @Bean
    public EventServiceImpl eventService() {
        return Mockito.mock(EventServiceImpl.class);
    }
    @Bean
    public EventOrganiserServiceImpl eventOrganiserService() {
        return Mockito.mock(EventOrganiserServiceImpl.class);
    }
    @Bean
    public EventTypeRepository eventTypeRepository() {
        return Mockito.mock(EventTypeRepository.class);
    }
    @Bean
    public FeaturedEventRepository featuredEventRepository() {
        return Mockito.mock(FeaturedEventRepository.class);
    }
    @Bean
    public EventRepository eventRepository() {
        return Mockito.mock(EventRepository.class);
    }
    @Bean
    public ArtistRepository artistRepository() {
        return Mockito.mock(ArtistRepository.class);
    }
    @Bean
    public JavaMailSenderImpl javaMailSender() {
        return Mockito.mock(JavaMailSenderImpl.class);
    }
    @Bean
    public TicketRepository ticketRepository() {
        return Mockito.mock(TicketRepository.class);
    }
    @Bean
    public PresaleInterestRepository presaleInterestRepository() {
        return Mockito.mock(PresaleInterestRepository.class);
    }
    @Bean
    public SectionRepository sectionRepository() {
        return Mockito.mock(SectionRepository.class);
    }
    @Bean
    public TicketPricingRepository ticketPricingRepository() {
        return Mockito.mock(TicketPricingRepository.class);
    }
    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }
    @Bean
    public QueueRepository queueRepository() {
        return Mockito.mock(QueueRepository.class);
    }
    @Bean
    public TicketCategoryRepository ticketCategoryRepository() {
        return Mockito.mock(TicketCategoryRepository.class);
    }
    @Bean
    public VenueRepository venueRepository() {
        return Mockito.mock(VenueRepository.class);
    }
}