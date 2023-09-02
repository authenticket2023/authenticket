package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.service.AdminService;
import com.authenticket.authenticket.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

}
