package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminDtoMapper adminDtoMapper;

    public List<AdminDisplayDto> findAllAdmin(){
        return adminRepository.findAll()
                .stream()
                .map(adminDtoMapper)
                .collect(Collectors.toList());
    }
    public Optional<AdminDisplayDto> findById(Integer adminId) {
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

    public EventOrganiser approveEventOrganiser(Integer organiserId, Admin admin) {
        Optional<EventOrganiser> eventOrganiserOptional = eventOrganiserRepository.findById(organiserId);

        if (eventOrganiserOptional.isPresent() ) {
            EventOrganiser eventOrg = eventOrganiserOptional.get();
            eventOrg.setAdmin(admin);
            eventOrganiserRepository.save(eventOrg);
            return eventOrg;
        }
        return null;
    }
}
