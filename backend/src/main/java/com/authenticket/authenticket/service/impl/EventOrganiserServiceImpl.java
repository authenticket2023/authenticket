package com.authenticket.authenticket.service.impl;


import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EventOrganiserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.authenticket.authenticket.service.Utility;
@Service
public class EventOrganiserServiceImpl extends Utility implements EventOrganiserService {
//    @Value("${authenticket.frontend-dev-url}")
//    private String apiUrl;

    @Value("${authenticket.frontend-production-url}")
    private String apiUrl;


    private final EventOrganiserRepository eventOrganiserRepository;
    private final EventOrganiserDtoMapper eventOrganiserDtoMapper;
    private final EmailServiceImpl emailService;
    private final AmazonS3Service amazonS3Service;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EventOrganiserServiceImpl(EventOrganiserRepository eventOrganiserRepository,
                                     EventOrganiserDtoMapper eventOrganiserDtoMapper,
                                     EmailServiceImpl emailService,
                                     AmazonS3Service amazonS3Service,
                                     PasswordEncoder passwordEncoder) {
        this.eventOrganiserRepository = eventOrganiserRepository;
        this.eventOrganiserDtoMapper = eventOrganiserDtoMapper;
        this.emailService = emailService;
        this.amazonS3Service = amazonS3Service;
        this.passwordEncoder = passwordEncoder;
    }

    public List<EventOrganiserDisplayDto> findAllEventOrganisers() {
        return eventOrganiserRepository.findAll()
                .stream()
                .map(eventOrganiserDtoMapper)
                .collect(Collectors.toList());
    }

    public List<Event> findAllEventsByOrganiser(Integer organiserId) {
        EventOrganiser organiser = eventOrganiserRepository.findById(organiserId).orElse(null);

        if (organiser != null) {
            return organiser.getEvents();
        }

        return new ArrayList<>();
    }

    public List<EventOrganiserDisplayDto> findEventOrganisersByReviewStatus(String status) {
        return eventOrganiserDtoMapper.map(eventOrganiserRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(status));
    }

    public Optional<EventOrganiserDisplayDto> findOrganiserById(Integer organiserId) {
        return eventOrganiserRepository.findById(organiserId).map(eventOrganiserDtoMapper);
    }

    public EventOrganiser saveEventOrganiser(EventOrganiser eventOrganiser) {
        return eventOrganiserRepository.save(eventOrganiser);
    }

    public EventOrganiser updateEventOrganiser(EventOrganiserUpdateDto eventOrganiserUpdateDto) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(eventOrganiserUpdateDto.organiserId());

        if (eventOrganiserOptional.isEmpty()) {
            throw new NonExistentException("Event Organiser with ID " + eventOrganiserUpdateDto.organiserId() + " does not exist");
        }

        EventOrganiser eventOrganiser = eventOrganiserOptional.get();
        eventOrganiserDtoMapper.update(eventOrganiserUpdateDto, eventOrganiser);

        String reviewStatus = eventOrganiserUpdateDto.reviewStatus();
        if (reviewStatus != null) {
            if(reviewStatus.equals("approved")) {
                //Generate password and update db
                String password = generateRandomPassword();
                eventOrganiser.setPassword(passwordEncoder.encode(password));

                //change link to log in page
                String link = apiUrl + "/OrganiserLogin";
                // Send email to organiser
                emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildOrganiserApprovalEmail(eventOrganiser.getName(), link, password, eventOrganiser.getReviewRemarks()), "Your account has been approved");
            } else if (reviewStatus.equals("rejected")){
                emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildOrganiserRejectionEmail(eventOrganiser.getName(), eventOrganiser.getReviewRemarks()), "Your account has been rejected");
            }
        }

        eventOrganiserRepository.save(eventOrganiser);
        return eventOrganiser;
    }

    @Override
    public EventOrganiser updateEventOrganiserImage(Integer organiserId, String filename) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            eventOrganiser.setLogoImage(filename);
            eventOrganiserRepository.save(eventOrganiser);
            return eventOrganiser;
        } else {
            return null;
        }

    }

    public String deleteEventOrganiser(Integer organiserId) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            if (eventOrganiser.getDeletedAt() != null) {
                return String.format("Event Organiser %d is already deleted.", organiserId);
            }

            eventOrganiser.setDeletedAt(LocalDateTime.now());
            eventOrganiserRepository.save(eventOrganiser);
            return String.format("Event Organiser %d is successfully deleted.", organiserId);
        } else {
            throw new NonExistentException("Event Organiser", organiserId);
        }
    }
}
