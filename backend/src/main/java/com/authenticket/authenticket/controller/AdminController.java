package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/admin")
public class AdminController extends Utility {

    private final AdminServiceImpl adminService;

    private final AdminDtoMapper adminDtoMapper;

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(AdminServiceImpl adminService,
                           AdminDtoMapper adminDtoMapper,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.adminDtoMapper = adminDtoMapper;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
    public ResponseEntity<GeneralApiResponse> findAdminById(@PathVariable(value = "admin_id") Integer admin_id){
        Optional<AdminDisplayDto> adminDisplayDto = adminService.findAdminById(admin_id);
        if(adminDisplayDto.isPresent()){
            return ResponseEntity.status(200).body(generateApiResponse(adminDisplayDto.get(), "Admin found"));
        }
        return ResponseEntity.status(404).body(generateApiResponse(null, "Admin does not exist"));
    }

    @PostMapping("/saveAdmin")
    public ResponseEntity<GeneralApiResponse> saveAdmin(@RequestParam(value = "name") String name,
                                                        @RequestParam("email") String email,
                                                        @RequestParam("password") String password) {
        if(adminRepository.findByEmail(email).isEmpty()){
//            try{
                Admin newAdmin = Admin
                        .builder()
                        .adminId(null)
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .build();
                Admin savedAdmin =  adminService.saveAdmin(newAdmin);
                return ResponseEntity.status(200).body(generateApiResponse(adminDtoMapper.apply(savedAdmin), "Admin has been saved"));
//            } catch (Exception e){
//                return ResponseEntity.status(400).body(generateApiResponse(null, "Something went wrong"));
//            }
        }
        return ResponseEntity.status(401).body(generateApiResponse(null, "Admin already exist"));
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<GeneralApiResponse> updateAdmin(@RequestBody Admin newAdmin){
        if(adminRepository.findByEmail(newAdmin.getEmail()).isPresent()){
            AdminDisplayDto updatedAdmin = adminService.updateAdmin(newAdmin);
            return ResponseEntity.status(200).body(generateApiResponse(updatedAdmin, "admin has been successfully updated"));
        }

        return ResponseEntity.status(404).body(generateApiResponse(null, "Admin does not exist"));
    }
}
