package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminUpdateDto;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<AdminDisplayDto> findAllAdmin();
    Optional<AdminDisplayDto> findEventById(Integer adminId);
    Admin saveAdmin(Admin admin);
    Admin updateAdmin(AdminUpdateDto adminUpdateDto);

    //would include registering of EventOrganiser
    EventOrganiser approveEventOrganiser (Integer organiserId, Admin admin);
}
