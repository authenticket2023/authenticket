package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.repository.ArtistRepository;
import com.authenticket.authenticket.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides the implementation of the `ArtistService` interface, which is responsible for managing artists, their information, and artist images.
 */

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    private final ArtistDtoMapper artistDtoMapper;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository,
                             ArtistDtoMapper artistDtoMapper){
        this.artistRepository = artistRepository;
        this.artistDtoMapper = artistDtoMapper;
    }

    /**
     * Retrieve a list of all artists.
     *
     * @return A list of artist information.
     */
    @Override
    public List<ArtistDisplayDto> findAllArtists() {
        return artistRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(artistDtoMapper)
                .collect(Collectors.toList());
    }

    /**
     * Find an artist by their unique identifier.
     *
     * @param artistId The unique identifier of the artist.
     * @return An optional containing artist information if found, or empty if not found.
     */
    @Override
    public Optional<ArtistDisplayDto> findByArtistId(Integer artistId) {
        return artistRepository.findById(artistId).map(artistDtoMapper);
    }

    /**
     * Save a new artist or update an existing artist.
     *
     * @param artist The artist object to save or update.
     * @return The saved artist.
     */
    @Override
    public Artist saveArtist(Artist artist){
        return artistRepository.save(artist);
    }

    /**
     * Update artist information.
     *
     * @param artist The artist information to update.
     * @return The updated artist information.
     */
    @Override
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

    /**
     * Delete an artist by setting the deletion timestamp.
     *
     * @param artistId The unique identifier of the artist to delete.
     * @throws AlreadyDeletedException if the artist is already deleted.
     * @throws NonExistentException if the artist does not exist.
     */
    @Override
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

    /**
     * Update the artist image filename for an artist.
     *
     * @param filename The new artist image filename.
     * @param artistId The unique identifier of the artist.
     * @return The updated artist information.
     */
    @Override
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
