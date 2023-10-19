package com.authenticket.authenticket.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A DTO for displaying comprehensive user information.
 */

public record UserFullDisplayDto(

    /**
    * The unique identifier for the user.
    */
    Integer userId,
                               
    /**
    * The name of the user.
    */
    String name,
                                 
    /**
    * The email address of the user.
    */
    String email,
                               
    /**
    * The date of birth of the user.
    */
    LocalDate dateOfBirth,
                                 
    /**
    * The profile image associated with the user.
    */
    String profileImage,
                                 
    /**
    * The role or user type (e.g., admin, regular user).
    */
    String role,
                                
    /**
    * Indicates whether the user is enabled or disabled.
    */
    Boolean enabled,
                                 
    /**
    * The date and time when the user account was created.
    */
    LocalDateTime createdAt,
                                 
    /**
    * The date and time when the user account was last updated.
    */
    LocalDateTime updatedAt,
                                 
    /**
    * The date and time when the user account was deleted (if applicable).
    */
    LocalDateTime deletedAt){
}
