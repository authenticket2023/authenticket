package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class ArtistServiceImpl {
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistDtoMapper artistDtoMapper;

    public Optional<ArtistDisplayDto> findById(Integer artistId) {
        return artistRepository.findById(artistId).map(artistDtoMapper);
    }

    public Artist saveArtist(Artist artist){
        return artistRepository.save(artist);
    }

    public ArtistDisplayDto updateVenue(Artist artist){
        Optional<Artist> artistOptional = artistRepository.findById(artist.getArtistId());

        if(artistOptional.isPresent()) {
            Artist existingArtist = artistOptional.get();
            artistDtoMapper.update(artist, existingArtist);
            artistRepository.save(existingArtist);
            return artistDtoMapper.apply(existingArtist);
        }

        return null;
    }

    public void deleteArtist(Integer artistId){
        Optional<Artist> artistOptional = artistRepository.findById(artistId);

        if (artistOptional.isPresent()) {
            Artist artist = artistOptional.get();
            if(artist.getDeletedAt()!=null){
                throw new AlreadyDeletedException("Artist already deleted");
            }

            artist.setDeletedAt(LocalDateTime.now());
            artistRepository.save(artist);

        } else {
            throw new NonExistentException("Artist does not exists");
        }
    }

    public ArtistDisplayDto updateArtistImage(String filename, Integer artistId){
        Optional<Artist> artistOptional = artistRepository.findById(artistId);

        if(artistOptional.isPresent()){
            Artist existingArtist = artistOptional.get();
            existingArtist.setArtistImage(filename);
            artistRepository.save(existingArtist);
            return artistDtoMapper.apply(existingArtist);
        }
        return null;
    }
}
