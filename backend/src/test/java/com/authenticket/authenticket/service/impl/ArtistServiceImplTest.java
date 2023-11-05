package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {
    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistDtoMapper artistDtoMapper;

    private ArtistServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new ArtistServiceImpl(artistRepository, artistDtoMapper);
    }
    @Test
    void testFindAllArtists() {
        // Arrange
        List<Artist> artistList = new ArrayList<>();
        // Mock the behavior of the artistRepository to return a list of artists
        when(artistRepository.findAllByDeletedAtIsNull()).thenReturn(artistList);

        List<ArtistDisplayDto> result = underTest.findAllArtists();

        // Assert that the artistList is not null and contains expected elements
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByArtistId() {
        Integer artistId = 1;
        Artist artist = new Artist();
        // Mock the behavior of the artistRepository to return an optional artist
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.of(artist));

        Optional<ArtistDisplayDto> artistDisplayDto = underTest.findByArtistId(1);

        // Assert that the optional is not empty and contains the expected ArtistDisplayDto
        assertTrue(artistDisplayDto.isPresent());
    }

    @Test
    void testSaveArtist() {
        Artist artist = Artist.builder()
                .artistId(99)
                        .artistName("GeorgiaTest")
                                .artistImage("null")
                                        .events(null)
                                                .build();
        // Mock the behavior of the artistRepository to return a saved artist
        when(artistRepository.save(artist)).thenReturn(artist);

        Artist savedArtist = underTest.saveArtist(artist);

        // Assert that the savedArtist is not null and contains the expected values
        assertNotNull(savedArtist);
    }

    @Test
    void testUpdateVenue() {
        Artist artist = Artist.builder()
                .artistId(99)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();
        ArgumentCaptor<Artist> artistArgumentCaptor =
                ArgumentCaptor.forClass(Artist.class);
        // Mock the behavior of the artistRepository to return an optional artist
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.of(artist));

        ArtistDisplayDto result = underTest.updateVenue(artist);
        verify(artistRepository).save(artistArgumentCaptor.capture());
        Artist updatedArtist = artistArgumentCaptor.getValue();
        // Assert that the updatedArtistDto is not null and contains the expected values

        assertNotNull(result);
        assertNotNull(updatedArtist);
    }

    @Test
    void testUpdateVenueWhenDoesNotExist() {
        Integer artistId = -1;
        Artist artist = Artist.builder()
                .artistId(artistId)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();

        ArtistDisplayDto result = underTest.updateVenue(artist);


        // Assert
        assertNull(result);
    }

    @Test
    void testDeleteArtist() {
        Integer artistId = 99;
        Artist artist = Artist.builder()
                .artistId(artistId)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();
        // Mock the behavior of the artistRepository to return an optional artist
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.of(artist));

        underTest.deleteArtist(artistId);

        assertNotNull(artist.getDeletedAt());
        verify(artistRepository).save(artist);
    }

    @Test
    void testDeleteArtistWhenAlreadyDeleted() {
        // Mock the behavior of the artistRepository to return an optional artist
        Integer artistId = 99;
        Artist artist = Artist.builder()
                .artistId(artistId)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();
        artist.setDeletedAt(LocalDateTime.now());
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.of(artist));

        // Assert that an AlreadyDeletedException is thrown
        assertThrows(AlreadyDeletedException.class, () -> underTest.deleteArtist(artistId));
    }

    @Test
    void testDeleteArtistWhenDoesNotExist() {
        // Mock the behavior of the artistRepository to return an empty optional
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        // Assert that a NonExistentException is thrown
        assertThrows(NonExistentException.class, () -> underTest.deleteArtist(1));
    }

    @Test
    void testUpdateArtistImage() {
        Integer artistId = 99;
        Artist artist = Artist.builder()
                .artistId(artistId)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();
        ArgumentCaptor<Artist> artistArgumentCaptor =
                ArgumentCaptor.forClass(Artist.class);
        // Mock the behavior of the artistRepository to return an optional artist
        when(artistRepository.findById(any(Integer.class))).thenReturn(Optional.of(artist));

        ArtistDisplayDto result = underTest.updateArtistImage("TestFile", artistId);
        verify(artistRepository).save(artistArgumentCaptor.capture());
        Artist updatedArtist= artistArgumentCaptor.getValue();

        // Assert
        assertNotNull(result);
        assertNotNull(updatedArtist);
    }

    @Test
    void testUpdateArtistImageWhenDoesNotExist() {
        Integer artistId = -1;
        Artist artist = Artist.builder()
                .artistId(artistId)
                .artistName("GeorgiaTest")
                .artistImage("null")
                .events(null)
                .build();

        ArtistDisplayDto result = underTest.updateArtistImage("null", artistId);


        // Assert
        assertNull(result);
    }
}