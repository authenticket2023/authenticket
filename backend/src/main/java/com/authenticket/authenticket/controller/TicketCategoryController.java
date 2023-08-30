package com.authenticket.authenticket.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/ticket-category")

public class TicketCategoryController {
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

}
