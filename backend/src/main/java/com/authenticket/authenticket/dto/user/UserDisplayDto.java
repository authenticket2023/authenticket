package com.authenticket.authenticket.dto.user;

import java.time.LocalDate;

/**
 * A DTO for displaying user information.
 */

public record UserDisplayDto(

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
    * The role or user type (e.g., admin, user).
    */
    String role){
}
