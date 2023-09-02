package com.authenticket.authenticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//    @CrossOrigin
@RequestMapping(path = "/registration")

public class UserController {
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

}
