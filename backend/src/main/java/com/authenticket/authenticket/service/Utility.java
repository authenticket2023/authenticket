package com.authenticket.authenticket.service;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.exception.NotApprovedException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;

/**
 * A utility class containing various helper methods for common tasks.
 */
public class Utility {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;
    @Autowired
    private AdminRepository adminRepository;

    /**
     * Gets the file extension based on the provided content type.
     *
     * @param contentType The content type of the file.
     * @return The file extension (e.g., ".jpg") corresponding to the content type.
     */
    public String getFileExtension(String contentType) {
        if (contentType == null) {
            return null;
        }
        if (contentType.equals("image/jpeg")) {
            return ".jpg";
        } else if (contentType.equals("image/png")) {
            return ".png";
        } else if (contentType.equals("image/gif")) {
            return ".gif";
        } // Add more cases for other supported file types
        return null; // Unsupported file type
    }

    /**
     * Generates a random alphanumeric password.
     *
     * @return A randomly generated alphanumeric password.
     */
    public String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    /**
     * Generates a general API response with optional data and a message.
     *
     * @param data    The data to include in the response (can be null).
     * @param message The message to include in the response.
     * @return A GeneralApiResponse object with the specified data and message.
     */
    public GeneralApiResponse<Object> generateApiResponse(Object data, String message) {
        if (data == null) {
            return GeneralApiResponse.builder()
                    .message(message)
                    .build();
        } else {
            return GeneralApiResponse.builder()
                    .message(message)
                    .data(data)
                    .build();
        }
    }

    /**
     * Checks if an event exists, is approved, and not deleted.
     *
     * @param eventId The ID of the event to check.
     * @throws NonExistentException  If the event does not exist.
     * @throws AlreadyDeletedException If the event is already deleted.
     * @throws NotApprovedException   If the event is not approved.
     */
    public void checkIfEventExistsAndIsApprovedAndNotDeleted(Integer eventId){
        Event event = eventRepository.findById(eventId).orElse(null);
        if(event == null){
            throw new NonExistentException("Event",eventId);
        } else if(event.getDeletedAt()!=null){
            throw new AlreadyDeletedException("Event", eventId);
        } else if(!Objects.equals(event.getReviewStatus(), "approved")){
            throw new NotApprovedException("Event",eventId);
        }
    }

    /**
     * Retrieves a User object from the request using the JWT token.
     *
     * @param request The HTTP servlet request containing the JWT token.
     * @return The User object derived from the JWT token.
     * @throws NonExistentException If the user cannot be derived from the token.
     */
    public User retrieveUserFromRequest(HttpServletRequest request) throws NonExistentException {
        String jwt = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(jwt);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new NonExistentException("Could not derive user from token in request header");
        }
        return userOptional.get();
    }

    /**
     * Retrieves an EventOrganiser object from the request using the JWT token.
     *
     * @param request The HTTP servlet request containing the JWT token.
     * @return The EventOrganiser object derived from the JWT token.
     * @throws NonExistentException If the organiser cannot be derived from the token.
     */
    public EventOrganiser retrieveOrganiserFromRequest(HttpServletRequest request) throws NonExistentException {
        String jwt = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(jwt);

        Optional<EventOrganiser> organiserOptional = eventOrganiserRepository.findByEmail(email);
        if (organiserOptional.isEmpty()) {
            throw new NonExistentException("Could not derive organiser from token in request header");
        }
        return organiserOptional.get();
    }

    /**
     * Retrieves an Admin object from the request using the JWT token.
     *
     * @param request The HTTP servlet request containing the JWT token.
     * @return The Admin object derived from the JWT token.
     * @throws NonExistentException If the admin cannot be derived from the token.
     */
    public Admin retrieveAdminFromRequest(HttpServletRequest request) throws NonExistentException {
        String jwt = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(jwt);

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isEmpty()) {
            throw new NonExistentException("Could not derive admin from token in request header");
        }
        return adminOptional.get();
    }

    /**
     * Checks if a request is made by an admin based on the JWT token.
     *
     * @param request The HTTP servlet request containing the JWT token.
     * @return True if the request is made by an admin; false otherwise.
     * @throws NonExistentException If the admin cannot be derived from the token.
     */
    public Boolean isAdminRequest(HttpServletRequest request) throws NonExistentException {
        String jwt = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(jwt);

        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        return adminOptional.isPresent();
    }
}
