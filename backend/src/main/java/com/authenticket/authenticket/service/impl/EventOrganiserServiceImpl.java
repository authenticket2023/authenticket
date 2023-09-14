package com.authenticket.authenticket.service.impl;


import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.AdminRepository;
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
    @Value("${authenticket.api-port}")
    private String apiPort;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private EventOrganiserDtoMapper eventOrganiserDtoMapper;

    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
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

    public List<EventOrganiser> findAllPendingOrganisers() {
        return eventOrganiserRepository.findByReviewStatusContains("pending");
    }

    public Optional<EventOrganiserDisplayDto> findOrganiserById(Integer organiserId) {
        return eventOrganiserRepository.findById(organiserId).map(eventOrganiserDtoMapper);
    }

    public EventOrganiser saveEventOrganiser(EventOrganiser eventOrganiser) {
        emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildOrganiserPendingEmail(eventOrganiser.getName()), "Your account is under review");

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
                String link = "http://localhost:" + apiPort + "/api/auth/register/";

                // Send email to organiser
                emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildOrganiserApprovalEmail(eventOrganiser.getName(), link, password, "good"), "Your account has been approved");
            } else if (reviewStatus.equals("rejected")){
                emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildOrganiserRejectionEmail(eventOrganiser.getName(),"bad one bro"), "Your account has been rejected");
            }
            else {
                throw new IllegalStateException("Review status in unknown state '" + reviewStatus + "'");
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

    public void deleteEventOrganiser(Integer organiserId) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            if (eventOrganiser.getDeletedAt() != null) {
                throw new AlreadyDeletedException("Event organiser already deleted");
            }

            eventOrganiser.setDeletedAt(LocalDateTime.now());
            eventOrganiserRepository.save(eventOrganiser);

        } else {
            throw new NonExistentException("Event organiser does not exists");
        }
    }

    public String removeEventOrganiser(Integer organiserId) {

        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent()) {
            EventOrganiser eventOrganiser = eventOrganiserOptional.get();
            String logoImage = eventOrganiser.getLogoImage();

            eventOrganiserRepository.deleteById(organiserId);
            if (logoImage != null) {
                amazonS3Service.deleteFile(logoImage, "event_organiser_profile");
            }


            return "event organiser removed successfully";
        }
        return "error: event organiser does not exist";

    }
}
