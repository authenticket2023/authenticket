package com.authenticket.authenticket;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController so spring knows what to do
@RestController
public class AuthenTicketController {
    //@RequestMapping default to root directory
    @RequestMapping
    public String helloWorld(){
        return "Hello world";
    }

    @RequestMapping("/goodbye")
    public String goodbye(){
        return "Goodbye";
    }
}
