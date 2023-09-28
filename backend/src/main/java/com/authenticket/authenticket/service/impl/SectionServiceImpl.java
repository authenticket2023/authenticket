package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.service.ArtistService;
import com.authenticket.authenticket.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;

//    private final SectionDtoMapper sectionDtoMapper;

    @Autowired
    public SectionServiceImpl(SectionRepository sectionRepository){
        this.sectionRepository = sectionRepository;
    }

//    public List<ArtistDisplayDto> findAllArtists() {
//        return artistRepository.findAll()
//                .stream()
//                .map(artistDtoMapper)
//                .collect(Collectors.toList());
//    }
//
//    public Optional<ArtistDisplayDto> findByArtistId(Integer artistId) {
//        return artistRepository.findById(artistId).map(artistDtoMapper);
//    }

    public Section saveSection(Section section){

        return sectionRepository.save(section);
    }

//    public ArtistDisplayDto updateVenue(Artist artist){
//        Optional<Artist> artistOptional = artistRepository.findById(artist.getArtistId());
//
//        if(artistOptional.isPresent()) {
//            Artist existingArtist = artistOptional.get();
//            artistDtoMapper.update(artist, existingArtist);
//            artistRepository.save(existingArtist);
//            return artistDtoMapper.apply(existingArtist);
//        }
//
//        return null;
//    }
//
//    public void deleteArtist(Integer artistId){
//        Optional<Artist> artistOptional = artistRepository.findById(artistId);
//
//        if (artistOptional.isPresent()) {
//            Artist artist = artistOptional.get();
//            if(artist.getDeletedAt()!=null){
//                throw new AlreadyDeletedException("Artist already deleted");
//            }
//
//            artist.setDeletedAt(LocalDateTime.now());
//            artistRepository.save(artist);
//
//        } else {
//            throw new NonExistentException("Artist does not exists");
//        }
//    }
//
//    public ArtistDisplayDto updateArtistImage(String filename, Integer artistId){
//        Optional<Artist> artistOptional = artistRepository.findById(artistId);
//
//        if(artistOptional.isPresent()){
//            Artist existingArtist = artistOptional.get();
//            existingArtist.setArtistImage(filename);
//            artistRepository.save(existingArtist);
//            return artistDtoMapper.apply(existingArtist);
//        }
//        return null;
//    }


}
