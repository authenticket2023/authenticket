package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private AdminDtoMapper adminDtoMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public ResponseEntity<GeneralApiResponse> findAdminById(@PathVariable("admin_id") Integer admin_id){
        Optional<AdminDisplayDto> adminDisplayDto = adminService.findById(admin_id);
        if(adminDisplayDto.isPresent()){
            GeneralApiResponse goodReq = GeneralApiResponse
                    .<AdminDisplayDto>builder()
                    .message("admin found")
                    .data(adminDisplayDto.get())
                    .build();
            return ResponseEntity.status(200).body(goodReq);
        }
        GeneralApiResponse badReq = GeneralApiResponse
                .builder()
                .message("admin does not exist")
                .build();
        return ResponseEntity.status(404).body(badReq);
    }

    @PostMapping("/saveAdmin")
    public ResponseEntity<GeneralApiResponse> saveAdmin(@RequestParam("name") String name,
                                                        @RequestParam("email") String email,
                                                        @RequestParam("password") String password) {
        if(!adminRepository.findByEmail(email).isPresent()){
            try{
                Admin newAdmin = Admin
                        .builder()
                        .adminId(null)
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .build();
                Admin savedAdmin =  adminService.saveAdmin(newAdmin);
                GeneralApiResponse goodReq = GeneralApiResponse
                        .<AdminDisplayDto>builder()
                        .message("admin has been saved")
                        .data(adminDtoMapper.apply(savedAdmin))
                        .build();
                return ResponseEntity.status(200).body(goodReq);
            } catch (Exception e){
                GeneralApiResponse badReq = GeneralApiResponse
                        .builder()
                        .message("something went wrong")
                        .build();
                return ResponseEntity.status(400).body(badReq);
            }
        }
        GeneralApiResponse badReq = GeneralApiResponse
                .builder()
                .message("Admin already exist")
                .build();
        return ResponseEntity.status(401).body(badReq);
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<GeneralApiResponse> updateAdmin(@RequestBody Admin newAdmin){
        if(adminRepository.findById(newAdmin.getAdminId()).isPresent()){
            AdminDisplayDto updatedAdmin = adminService.updateAdmin(newAdmin);
            GeneralApiResponse goodReq = GeneralApiResponse
                    .<AdminDisplayDto>builder()
                    .message("admin has been successfully updated")
                    .data(updatedAdmin)
                    .build();
            return ResponseEntity.status(200).body(goodReq);
        }
        GeneralApiResponse badReq = GeneralApiResponse
                .builder()
                .message("admin does not exist")
                .build();
        return ResponseEntity.status(401).body(badReq);
    }
}
