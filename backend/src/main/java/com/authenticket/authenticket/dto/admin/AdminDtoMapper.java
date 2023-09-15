package com.authenticket.authenticket.dto.admin;

import com.authenticket.authenticket.model.Admin;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AdminDtoMapper implements Function<Admin, AdminDisplayDto> {
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
            oldAdmin.setPassword(newAdmin.getPassword());
        }
    }
}
