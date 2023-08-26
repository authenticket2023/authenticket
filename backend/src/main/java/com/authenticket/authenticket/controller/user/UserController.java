package com.authenticket.authenticket.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;



    @RestController
//    @CrossOrigin
    @RequestMapping(path = "/registration")

    public class UserController {
        @GetMapping("/test")
        public String test() {
          return "test successful";
        }

    }