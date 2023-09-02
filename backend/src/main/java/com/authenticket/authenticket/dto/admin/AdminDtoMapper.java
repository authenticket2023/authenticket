package com.authenticket.authenticket.dto.admin;

import com.authenticket.authenticket.model.Admin;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AdminDtoMapper implements Function<Admin, AdminDisplayDto> {
    public AdminDisplayDto apply(Admin admin) {
        return new AdminDisplayDto(
                admin.getAdminId(),
                admin.getUsername(),
                admin.getEmail()
        );
    }

    public void update(AdminUpdateDto dto, Admin admin){
        if(dto.name() != null){
            admin.setName(dto.name());
        }
        if(dto.password() != null){
            admin.setPassword(dto.password());
        }
    }
}
