package com.authenticket.authenticket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController so spring knows what to do
@RestController
public class AuthenTicketController {
    //@RequestMapping default to root directory
    @GetMapping
    public String helloWorld(){
        return "Hello world";
    }

    @GetMapping("/goodbye")
    public String goodbye(){
        return "Goodbye";
    }
}
