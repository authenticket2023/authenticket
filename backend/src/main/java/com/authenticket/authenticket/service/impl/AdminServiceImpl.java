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

@Service
public class AdminServiceImpl implements AdminService, UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final AdminRepository adminRepository;

    private final AdminDtoMapper adminDtoMapper;


    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository,
                             AdminDtoMapper adminDtoMapper){
        this.adminRepository = adminRepository;
        this.adminDtoMapper = adminDtoMapper;
    }



    public List<AdminDisplayDto> findAllAdmin(){
        return adminRepository.findAll()
                .stream()
                .map(adminDtoMapper)
                .collect(Collectors.toList());
    }
    public Optional<AdminDisplayDto> findAdminById(Integer adminId) {
        return adminRepository.findById(adminId).map(adminDtoMapper);
    }
    public Admin saveAdmin(Admin admin){
        return adminRepository.save(admin);
    }
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

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MSG, email)));
    }
}
