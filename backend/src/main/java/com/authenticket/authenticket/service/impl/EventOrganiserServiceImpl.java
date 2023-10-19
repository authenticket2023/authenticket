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
import com.authenticket.authenticket.repository.EventRepository;
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
    // @Value("${authenticket.frontend-dev-url}")
    // private String apiUrl;

    @Value("${authenticket.frontend-production-url}")
    private String apiUrl;



    private final EventRepository eventRepository;

    private final EventOrganiserRepository eventOrganiserRepository;
    private final EventOrganiserDtoMapper eventOrganiserDtoMapper;
    private final EmailServiceImpl emailService;
    private final AmazonS3Service amazonS3Service;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs an instance of EventOrganiserServiceImpl with the specified
     * dependencies.
     * This constructor is used for dependency injection to set various
     * repositories,
     * mappers, services, and other dependencies required for managing event
     * organisers in the application.
     *
     * @param eventOrganiserRepository The EventOrganiserRepository to interact with
     *                                 event organiser data.
     * @param eventOrganiserDtoMapper  The EventOrganiserDtoMapper for mapping Event
     *                                 Organiser entities to DTOs.
     * @param emailService             The EmailServiceImpl for sending emails.
     * @param amazonS3Service          The AmazonS3Service for interacting with
     *                                 Amazon S3 storage.
     * @param passwordEncoder          The PasswordEncoder for encoding and decoding
     *                                 passwords.
     */
    @Autowired
    public EventOrganiserServiceImpl(EventOrganiserRepository eventOrganiserRepository,
                                     EventRepository eventRepository,
                                     EventOrganiserDtoMapper eventOrganiserDtoMapper,
                                     EmailServiceImpl emailService,
                                     AmazonS3Service amazonS3Service,
                                     PasswordEncoder passwordEncoder) {
        this.eventOrganiserRepository = eventOrganiserRepository;
        this.eventOrganiserDtoMapper = eventOrganiserDtoMapper;
        this.eventRepository = eventRepository;
        this.emailService = emailService;
        this.amazonS3Service = amazonS3Service;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a list of Event Organisers from the database and maps them to
     * EventOrganiserDisplayDto objects.
     * This method returns a list of EventOrganiserDisplayDto representing the event
     * organisers.
     *
     * @return A List of EventOrganiserDisplayDto containing information about the
     *         event organisers.
     */
    @Override
    public List<EventOrganiserDisplayDto> findAllEventOrganisers() {
        return eventOrganiserRepository.findAll()
                .stream()
                .map(eventOrganiserDtoMapper)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of events organized by a specific Event Organiser identified
     * by their unique ID.
     *
     * @param organiserId The unique ID of the Event Organiser whose organized
     *                    events are to be retrieved.
     * @return A List of Event objects representing the events organized by the
     *         specified Event Organiser.
     *         If the Event Organiser with the given ID is not found, an empty list
     *         is returned.
     */
    @Override
    public List<Event> findAllEventsByOrganiser(Integer organiserId) {
        EventOrganiser organiser = eventOrganiserRepository.findById(organiserId).orElse(null);

        if (organiser != null) {
            return organiser.getEvents();
        }

        return new ArrayList<>();
    }

    /**
     * Retrieves a list of Event Organisers based on their review status and sorts
     * them by creation date in ascending order.
     *
     * @param status The review status to filter Event Organisers (e.g., "approved,"
     *               "pending," "rejected").
     * @return A List of EventOrganiserDisplayDto objects representing Event
     *         Organisers that match the specified review status.
     */
    @Override
    public List<Event> findAllCurrentEventsByOrganiser(Integer organiserId) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Event> eventList = eventRepository.findAllByReviewStatusAndOrganiserOrganiserIdAndEventDateIsAfterAndDeletedAtIsNullOrderByEventDateAsc(Event.ReviewStatus.APPROVED.getStatusValue(), organiserId,currentDate);

        return eventList;
    }

    @Override
    public List<EventOrganiserDisplayDto> findEventOrganisersByReviewStatus(String status) {
        return eventOrganiserDtoMapper
                .map(eventOrganiserRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(status));
    }

    /**
     * Retrieves an Event Organiser by their unique identifier and returns it as an
     * Optional of EventOrganiserDisplayDto.
     *
     * @param organiserId The unique identifier of the Event Organiser to retrieve.
     * @return An Optional containing an EventOrganiserDisplayDto if an Event
     *         Organiser with the specified ID is found, or an empty Optional if not
     *         found.
     */
    @Override
    public Optional<EventOrganiserDisplayDto> findOrganiserById(Integer organiserId) {
        return eventOrganiserRepository.findById(organiserId).map(eventOrganiserDtoMapper);
    }

    /**
     * Saves an Event Organiser in the database.
     *
     * @param eventOrganiser The Event Organiser to be saved.
     * @return The saved Event Organiser, including any generated unique
     *         identifiers.
     */
    @Override
    public EventOrganiser saveEventOrganiser(EventOrganiser eventOrganiser) {
        return eventOrganiserRepository.save(eventOrganiser);
    }

    /**
     * Updates an existing Event Organiser based on the information provided in the
     * EventOrganiserUpdateDto.
     * 
     * @param eventOrganiserUpdateDto The data containing the updates for the Event
     *                                Organiser.
     * @return The updated Event Organiser after the modifications.
     * @throws NonExistentException If the Event Organiser with the specified ID
     *                              does not exist.
     */
    @Override
    public EventOrganiser updateEventOrganiser(EventOrganiserUpdateDto eventOrganiserUpdateDto) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository
                .findById(eventOrganiserUpdateDto.organiserId());

        if (eventOrganiserOptional.isEmpty()) {
            throw new NonExistentException(
                    "Event Organiser with ID " + eventOrganiserUpdateDto.organiserId() + " does not exist");
        }

        EventOrganiser eventOrganiser = eventOrganiserOptional.get();
        eventOrganiserDtoMapper.update(eventOrganiserUpdateDto, eventOrganiser);

        String reviewStatus = eventOrganiserUpdateDto.reviewStatus();
        if (reviewStatus != null) {
            if (reviewStatus.equals("approved")) {
                // Generate password and update db
                String password = generateRandomPassword();
                eventOrganiser.setPassword(passwordEncoder.encode(password));

                // change link to log in page
                String link = apiUrl + "/OrganiserLogin";
                // Send email to organiser
                emailService.send(eventOrganiser.getEmail(),
                        EmailServiceImpl.buildOrganiserApprovalEmail(eventOrganiser.getName(), link, password,
                                eventOrganiser.getReviewRemarks()),
                        "Your account has been approved");
            } else if (reviewStatus.equals("rejected")) {
                emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildOrganiserRejectionEmail(
                        eventOrganiser.getName(), eventOrganiser.getReviewRemarks()), "Your account has been rejected");
            }
        }

        eventOrganiserRepository.save(eventOrganiser);
        return eventOrganiser;
    }

    /**
     * Updates the logo image of an existing Event Organiser.
     *
     * @param organiserId The unique identifier of the Event Organiser.
     * @param filename    The new filename of the logo image to be associated with
     *                    the Event Organiser.
     * @return The updated Event Organiser after setting the new logo image.
     */
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

    /**
     * Deletes an Event Organiser by marking it as deleted in the database.
     *
     * @param organiserId The unique identifier of the Event Organiser to be
     *                    deleted.
     * @return A message indicating the result of the deletion operation, including
     *         success or an error message.
     *         If the Event Organiser is already deleted, it returns a message
     *         indicating that it's already deleted.
     * @throws NonExistentException if there is no Event Organiser with the
     *                              specified ID.
     */
    @Override
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
