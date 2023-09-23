package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    @Modifying(clearAutomatically = true)
    public void testEnableAppUser() {
        // Create a test user and save it to the database
        String email = "test@example.com";
        User user = User.builder()
                .userId(99)
                .name("Georgia")
                .email(email)
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .tickets(null)
                .build();

        underTest.save(user);

        // Enable the user using the enableAppUser method
        underTest.enableAppUser(email);

        // Fetch the user from the database again
        Optional<User> enabledUser = underTest.findByEmail(email);

        // Check if the user is now enabled
        assertThat(enabledUser.isPresent()).isTrue();
        assertThat(enabledUser.get().isEnabled()).isTrue();
    }
}