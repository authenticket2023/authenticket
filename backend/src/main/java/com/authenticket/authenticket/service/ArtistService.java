package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.model.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistService {
    List<ArtistDisplayDto> findAllArtists();
    Optional<ArtistDisplayDto> findByArtistId(Integer artistId);
    Artist saveArtist(Artist artist);
    ArtistDisplayDto updateVenue(Artist artist);
    void deleteArtist(Integer artistId);
    ArtistDisplayDto updateArtistImage(String filename, Integer artistId);
}
