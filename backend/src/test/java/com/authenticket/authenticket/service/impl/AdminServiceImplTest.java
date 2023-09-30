package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminServiceImpl underTest;

    @BeforeEach
    void setUp() {
        AdminDtoMapper adminDtoMapper = new AdminDtoMapper(passwordEncoder);
        underTest = new AdminServiceImpl(adminRepository, adminDtoMapper);
    }

    @Test
    void testFindAllAdmin() {
        // Arrange
        List<Admin> adminList = new ArrayList<>();
        when(adminRepository.findAll()).thenReturn(adminList);

        // Act
        List<AdminDisplayDto> result = underTest.findAllAdmin();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindAdminById() {
        // Arrange
        Integer adminId = 1;
        Admin admin = new Admin();
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        // Act
        Optional<AdminDisplayDto> result = underTest.findAdminById(adminId);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void testSaveAdmin() {
        // Arrange
        Admin admin = Admin.builder()
                .adminId(99)
                .name("GeorgiaTest")
                .email("test@example.com")
                .password("password1")
                .eventOrganiser(null)
                .build();
        when(adminRepository.save(admin)).thenReturn(admin);

        // Act
        Admin savedAdmin = underTest.saveAdmin(admin);

        // Assert
        assertNotNull(savedAdmin);
    }

    @Test
    void testUpdateAdminWhenAdminExist() {
        // Arrange
        Integer userId = 99;
        String email = "test@example.com";
        Admin newAdmin = Admin.builder()
                .adminId(userId)
                .name("GeorgiaTest")
                .email(email)
                .password("password1")
                .eventOrganiser(null)
                .build();

        Admin existingAdmin = Admin.builder()
                .adminId(userId)
                .name("UpdatedGeorgia")
                .email(email)
                .password("password12")
                .eventOrganiser(null)
                .build();

        ArgumentCaptor<Admin> adminArgumentCaptor =
                ArgumentCaptor.forClass(Admin.class);

        // Act
        // Mock the userRepository behavior
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(existingAdmin));

        // Mocking the behavior of userDtoMapper.update (assuming it doesn't throw exceptions)
        AdminDisplayDto result = underTest.updateAdmin(newAdmin);
        verify(adminRepository).save(adminArgumentCaptor.capture());
        Admin updatedAdmin= adminArgumentCaptor.getValue();

        // Assert
        assertNotNull(result);
        assertNotNull(updatedAdmin);
    }

    @Test
    void testUpdateAdminWhenAdminDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        Admin newAdmin = Admin.builder()
                .adminId(-1)
                .name("AdminGeorgia")
                .email("test@example.com")
                .password("admin")
                .eventOrganiser(null)
                .build();

        // Act
        AdminDisplayDto result = underTest.updateAdmin(newAdmin);

        // Act & Assert
        assertNull(result);
    }

    @Test
    void testLoadUserByUsername() {
        // Arrange
        String email = "test@example.com";
        Admin admin = new Admin();
        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        // Act
        UserDetails userDetails = underTest.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> underTest.loadUserByUsername(email));
    }
}