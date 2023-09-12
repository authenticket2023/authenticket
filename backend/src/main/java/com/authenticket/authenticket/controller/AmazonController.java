package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/aws")
public class AmazonController {

    @Autowired
    private AmazonS3ServiceImpl service;


    @PostMapping("/uploadFile")
    public ResponseEntity<String>  fileUpload(@RequestParam("file") MultipartFile file,
                                              @RequestParam("imageName") String imageName,
                                              @RequestParam("file-type") String fileType){
        return new ResponseEntity<> (service.uploadFile(file, imageName, fileType), HttpStatus.OK);
    }
    @DeleteMapping("/deleteFile")
    public ResponseEntity<String> fileDelete(@RequestParam("imageName") String imageName,
                                             @RequestParam("file-type") String fileType) {
        return new ResponseEntity<> (service.deleteFile(imageName, fileType), HttpStatus.OK);
    }

    @GetMapping("/displayFile")
    public ResponseEntity<String>  fileDisplay(@RequestParam("imageName") String imageName,
                                              @RequestParam("file-type") String fileType){
        return new ResponseEntity<> (service.displayFile(imageName, fileType), HttpStatus.OK);
    }

}
