package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.model.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<AdminDisplayDto> findAllAdmin();
    Optional<AdminDisplayDto> findAdminById(Integer adminId);
    Admin saveAdmin(Admin admin);
    AdminDisplayDto updateAdmin(Admin newAdmin);

    //would include verifying registering of EventOrganiser
}
