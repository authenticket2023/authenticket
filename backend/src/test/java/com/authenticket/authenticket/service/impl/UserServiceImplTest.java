package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDtoMapper userDtoMapper;
    private UserServiceImpl underTest;

    @BeforeEach
    void setUp(){
        underTest = new UserServiceImpl(userRepository, userDtoMapper);
    }

    //Test for findAllUser
    @Test
    void canFindAllUser() {
        // Arrange: Set up any necessary mock behaviors for userRepository
        List<User> mockUserList = new ArrayList<>(); // Create a list of mock User objects
        when(userRepository.findAll()).thenReturn(mockUserList); // Mock the behavior of findAll

        // Act: Call the method being tested
        List<UserFullDisplayDto> result = underTest.findAllUser();

        // Assert: Verify that the expected interactions occurred
        verify(userRepository).findAll();
        assertEquals(mockUserList.size(), result.size());
    }

    //Test for loadUserByUsername
    @Test
    void canLoadUserByUsernameWhenUserExists() {
        // Arrange
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

        // Mock the userRepository behavior
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = underTest.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        // Add more assertions based on what you expect UserDetails to contain
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        // Mock the userRepository behavior to return an empty Optional
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> underTest.loadUserByUsername(email));
    }

    // Test for findById
    //not working
    @Test
    void canFindUserByIdWhenUserExists() {
        // Arrange
        Integer userId = 99;
        User user = User.builder()
                .userId(userId)
                .name("Georgia")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .tickets(null)
                .build();

        userRepository.save(user);
        // Mock the userRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act: Find by given id
        Optional<UserFullDisplayDto> userDisplayDto = underTest.findById(userId);

        // Assert
        assertTrue(userDisplayDto.isPresent());
        assertEquals(userId, userDisplayDto.get().userId());
        // Add more assertions based on what you expect in the UserFullDisplayDto
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        // Arrange
        Integer userId = -1;

        // Mock the userRepository behavior to return an empty Optional
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserFullDisplayDto> userDtoOptional = underTest.findById(userId);

        // Assert
        assertFalse(userDtoOptional.isPresent());
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateProfileImage() {
    }
}