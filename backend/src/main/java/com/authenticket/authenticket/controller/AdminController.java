package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private AdminRepository adminRepository;

    //have to implement response entity
    @GetMapping("/test")
    public String test() {

        return "test successful";
    }

    @GetMapping
    public List<AdminDisplayDto> findAllAdmin() {
        return adminService.findAllAdmin();
    }

    @GetMapping("/{admin_id}")
    public Optional<AdminDisplayDto> findAdminById(@PathVariable("admin_id") Integer admin_id) {
        return adminService.findById(admin_id);
    }

    @PostMapping("/saveAdmin")
    public Admin saveAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }

    @PutMapping("/updateAdmin")
    public AdminDisplayDto updateAdmin(@RequestBody Admin newAdmin) {
        return adminService.updateAdmin(newAdmin);
    }

    @DeleteMapping("/approveEventOrganiser")
    public EventOrganiser approveEventOrganiser(@RequestParam("organiserId") Integer event_id,
                                                @RequestParam("adminId") Integer adminId) {
        Optional<Admin> currentAdmin = adminRepository.findById(adminId);
        if(currentAdmin.isPresent()){
            Admin existingAdmin = currentAdmin.get();
            return adminService.approveEventOrganiser(event_id, existingAdmin);
        }
        return null;
    }

}
