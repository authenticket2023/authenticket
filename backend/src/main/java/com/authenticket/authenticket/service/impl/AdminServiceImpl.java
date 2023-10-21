package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides the implementation of the `AdminService` and `UserDetailsService` interfaces.
 * It is responsible for handling admin-related operations and authentication using Spring Security.
 */

@Service
public class AdminServiceImpl implements AdminService, UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final AdminRepository adminRepository;

    private final AdminDtoMapper adminDtoMapper;

    /**
     * Constructs a new `AdminServiceImpl` with the provided `AdminRepository` and `AdminDtoMapper`.
     *
     * @param adminRepository The repository for managing admin data.
     * @param adminDtoMapper  The mapper for converting between DTOs and entities.
     */
    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository,
                             AdminDtoMapper adminDtoMapper){
        this.adminRepository = adminRepository;
        this.adminDtoMapper = adminDtoMapper;
    }

    /**
     * Retrieve a list of all admin users and convert them into display DTOs.
     *
     * @return A list of `AdminDisplayDto` representing all admin users.
     */
    @Override
    public List<AdminDisplayDto> findAllAdmin(){
        return adminRepository.findAll()
                .stream()
                .map(adminDtoMapper)
                .collect(Collectors.toList());
    }

    /**
     * Find an admin user by their unique identifier and convert it into a display DTO.
     *
     * @param adminId The unique identifier of the admin user.
     * @return An `Optional` containing the `AdminDisplayDto` of the admin user if found, or an empty `Optional` if not.
     */
    @Override
    public Optional<AdminDisplayDto> findAdminById(Integer adminId) {
        return adminRepository.findById(adminId).map(adminDtoMapper);
    }

    /**
     * Save an admin user to the repository.
     *
     * @param admin The admin user to be saved.
     * @return The saved `Admin` entity.
     */
    @Override
    public Admin saveAdmin(Admin admin){
        return adminRepository.save(admin);
    }

    /**
     * Update an admin user's information.
     *
     * @param newAdmin The updated admin user information.
     * @return The `AdminDisplayDto` representing the updated admin user, or `null` if the user was not found.
     */
    @Override
    public AdminDisplayDto updateAdmin(Admin newAdmin){
        Optional<Admin> adminOptional = adminRepository.findByEmail(newAdmin.getEmail());

        if(adminOptional.isPresent()){
            Admin existingAdmin = adminOptional.get();
            adminDtoMapper.update(newAdmin, existingAdmin);
            adminRepository.save(existingAdmin);
            return adminDtoMapper.apply(existingAdmin);
        }

        return null;
    }

    /**
     * Load a user's details using their email address.
     *
     * @param email The email address of the user.
     * @return The user details (a `UserDetails` instance) if found.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MSG, email)));
    }
}
