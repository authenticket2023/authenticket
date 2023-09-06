//package com.authenticket.authenticket.controller;
//
//import com.authenticket.authenticket.exception.NonExistentException;
//import com.authenticket.authenticket.model.Artist;
////import com.authenticket.authenticket.model.ArtistEvent;
//import com.authenticket.authenticket.model.Event;
////import com.authenticket.authenticket.repository.ArtistEventRepository;
//import com.authenticket.authenticket.repository.ArtistRepository;
//import com.authenticket.authenticket.repository.EventRepository;
//import com.authenticket.authenticket.service.Utility;
////import com.authenticket.authenticket.service.impl.ArtistEventServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping(path = "/api/artist-event")
//public class ArtistEventController extends Utility {
//    @Autowired
//    private ArtistEventRepository artistEventRepository;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private ArtistRepository artistRepository;
//
//    @Autowired
//    private ArtistEventServiceImpl artistEventService;
//
//    @GetMapping("/test")
//    public String test() {
//        return "test successful";
//    }
//
//    @PostMapping("/assign")
//    public ResponseEntity<GeneralApiResponse> assignArtistToEvent(
//            @RequestParam("eventId") Integer eventId,
//            @RequestParam("artistId") Integer artistId) {
//        Optional<Event> eventOptional = eventRepository.findById(eventId);
//        Optional<Artist> artistOptional = artistRepository.findById(artistId);
//
//        if (artistOptional.isPresent() && eventOptional.isPresent()) {
//            Event event = eventOptional.get();
//            Artist artist = artistOptional.get();
//            ArtistEvent artistEvent = artistEventService.assignArtistToEvent(event, artist);
//            return ResponseEntity.ok(generateApiResponse(artistEvent, "Artist successfully assigned to event"));
//
//        } else {
//            if (artistOptional.isEmpty()) {
//                throw new NonExistentException("Artist does not exists");
//            } else {
//                throw new NonExistentException("Event does not exists");
//            }
//        }
//
//    }
//}
