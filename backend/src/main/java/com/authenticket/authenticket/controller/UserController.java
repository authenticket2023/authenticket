package com.authenticket.authenticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;



    @RestController
    @CrossOrigin
    @RequestMapping("/user")

    public class UserController {

//        @Autowired
//        private PublisherRepository publisherRepository;

//        @PostMapping("/create")
//        public Publisher create(@RequestBody Publisher publisher) {
//            return publisherRepository.save(publisher);
//        }
//
        @GetMapping("/test")
        public String test() {
          return "test successful";
        }
//
//        @GetMapping("/{id}")
//        public Optional<Publisher> findOneById(@PathVariable String id) {
//            return publisherRepository.findById(id);
//        }
//
//        @PutMapping("/update")
//        public Publisher update(@RequestBody Publisher publisher) {
//            return publisherRepository.save(publisher);
//        }
//
//        @DeleteMapping("/delete/{id}")
//        public void deleteById(@PathVariable String id) {
//            publisherRepository.deleteById(id);
//        }

    }