package com.authenticket.authenticket.dto.admin;

import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AdminDtoMapper implements Function<Admin, AdminDisplayDto>  {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminDtoMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }

    public AdminDisplayDto apply(Admin admin) {
        return new AdminDisplayDto(
                admin.getAdminId(),
                admin.getName(),
                admin.getEmail(),
                Admin.getRole(),
                admin.getCreatedAt(),
                admin.getUpdatedAt(),
                admin.getDeletedAt()
        );
    }

    public void update(Admin newAdmin, Admin oldAdmin){
        if(newAdmin.getName() != null){
            oldAdmin.setName(newAdmin.getName());
        }
        if(newAdmin.getPassword() != null){
            oldAdmin.setPassword(passwordEncoder.encode(newAdmin.getPassword()));
        }
    }
}
