package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.dto.user.UserDtoMapper;
import com.authenticket.authenticket.dto.user.UserFullDisplayDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl underTest;

    @BeforeEach
    void setUp(){
        UserDtoMapper userDtoMapper = new UserDtoMapper(passwordEncoder);
        underTest = new UserServiceImpl(userRepository, userDtoMapper);
    }

    //Test for findAllUser
    @Test
    void testFindAllUser() {
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
    void testLoadUserByUsernameWhenUserExists() {
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
    void testThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
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
    void testFindUserByIdWhenUserExists() {
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
                .build();


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
    void testFindUserByIdWhenUserDoesNotExist() {
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
    void testUpdateUserWhenUserExists() {
        // Arrange
        Integer userId = 99;
        String email = "test@example.com";
        User newUser = User.builder()
                .userId(userId)
                .name("GeorgiaTest")
                .email(email)
                .password("password1")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                //.tickets(null)
                .build();

        User existingUser = User.builder()
                .userId(userId)
                .name("UpdatedGeorgia")
                .email(email)
                .password("update")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                //.tickets(null)
                .build();

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        // Mock the userRepository behavior
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Mocking the behavior of userDtoMapper.update (assuming it doesn't throw exceptions)
        UserDisplayDto result = underTest.updateUser(newUser);
        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser= userArgumentCaptor.getValue();

        // Assert
        assertNotNull(result);
        assertNotNull(updatedUser);
    }

    @Test
    public void testUpdateUserWhenUserDoesNotExist() {
        // Arrange
        User nonExistingUser = User.builder()
                .userId(-1)
                .name("UpdatedGeorgia")
                .email("test@example.com")
                .password("update")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();

        // Act
        UserDisplayDto result = underTest.updateUser(nonExistingUser);

        // Assert
        assertNull(result);
    }

    @Test
    public void testDeleteUserWhenUserExistsAndNotDeleted() {
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
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        underTest.deleteUser(userId);

        // Assert
        assertNotNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }

    @Test
    public void testDeleteUserWhenUserExistsAndAlreadyDeleted() {
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
                .build();
        user.setDeletedAt(LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act and Assert
        assertThrows(AlreadyDeletedException.class, () -> underTest.deleteUser(userId));
    }

    @Test
    public void testDeleteUserWhenUserDoesNotExist() {
        // Arrange
        Integer userId = -1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> underTest.deleteUser(userId));
    }

    @Test
    public void testUpdateProfileImageWhenUserExists() {
        // Arrange
        Integer userId = 99;
        String filename = "image.jpg";
        User newUser = User.builder()
                .userId(userId)
                .name("GeorgiaTest")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(filename)
                .enabled(false)
                .build();

        User existingUser = User.builder()
                .userId(userId)
                .name("GeorgiaTest")
                .email("test@example.com")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .profileImage(null)
                .enabled(false)
                .build();

        ArgumentCaptor<User> userArgumentCaptor =
                ArgumentCaptor.forClass(User.class);

        // Act
        // Mock the userRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Mocking the behavior of userDtoMapper.update (assuming it doesn't throw exceptions)
        UserDisplayDto result = underTest.updateProfileImage(filename, userId);
        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser= userArgumentCaptor.getValue();

        // Assert
        assertNotNull(result);
        assertEquals(newUser, updatedUser);
    }

    @Test
    public void testUpdateProfileImageWhenUserDoesNotExist() {
        // Arrange
        Integer userId = -1;
        String filename = "image.jpg";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        UserDisplayDto result = underTest.updateProfileImage(filename, userId);

        // Assert
        assertNull(result);
    }
}