package com.authenticket.authenticket.controller.demo;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/demo-controller")
@RequiredArgsConstructor
public class DemoController {
    private final AmazonS3 amazonS3;
    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello from secured endpoint");
    }

    @RequestMapping(method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadTest(@RequestParam("files") MultipartFile files) {
        File convFile = new File(Objects.requireNonNull(files.getOriginalFilename()));
        amazonS3.putObject("authenticket", "test", convFile);
        return ResponseEntity.status(200).body(convFile.getName());
    }
}
