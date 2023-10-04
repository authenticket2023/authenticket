package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceImplTest {
    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueDtoMapper venueDtoMapper;

    private VenueServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new VenueServiceImpl(venueRepository, venueDtoMapper);
    }

    @Test
    public void testFindByIdWhenVenueExists() {
        // Arrange
        Integer venueId = 99;
        Venue venue = Venue.builder()
                .venueId(venueId)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));

        // Act
        Optional<Venue> venueDisplay = underTest.findById(venueId);

        // Assert
        assertTrue(venueDisplay.isPresent());
        assertEquals(venueId, venueDisplay.get().getVenueId());
    }

    @Test
    public void testFindByIdWhenVenueDoesNotExist() {
        // Arrange
        Integer venueId = -1;

        // Mock the userRepository behavior to return an empty Optional
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        // Act
        Optional<Venue> venueOptional = underTest.findById(venueId);

        // Assert
        assertFalse(venueOptional.isPresent());
    }

    @Test
    public void testFindAllVenue() {
        // Arrange
        List<Venue> mockVenueList = new ArrayList<>();
        when(venueRepository.findAll()).thenReturn(mockVenueList);

        // Act
        List<Venue> result = underTest.findAllVenue();

        // Assert
        verify(venueRepository).findAll();
        assertEquals(mockVenueList.size(), result.size());
    }

    @Test
    public void testSaveVenue() {
        // Arrange
        Integer venueId = 99;
        Venue venue = Venue.builder()
                .venueId(venueId)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();
        ArgumentCaptor<Venue> venueArgumentCaptor =
                ArgumentCaptor.forClass(Venue.class);

        // Mock the userRepository behavior
        when(venueRepository.save(any(Venue.class))).thenReturn(venue);

        // Act
        Venue result = underTest.saveVenue(venue);

        // Assert
        assertNotNull(result);
        assertThat(result).isEqualTo(venue);

        // Verify that the save method is called once with the expected venue
        verify(venueRepository).save(venueArgumentCaptor.capture());
        Venue addedVenue = venueArgumentCaptor.getValue();
        assertThat(addedVenue).isEqualTo(venue);
    }


    @Test
    public void testUpdateVenueWhenVenueExists() {
        // Arrange
        Integer venueId = 99;
        Venue existingVenue = Venue.builder()
                .venueId(venueId)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();

        Venue newVenue = Venue.builder()
                .venueId(venueId)
                .venueName("New Venue Name")
                .venueLocation("New Venue Location")
                .venueImage(null)
                .build();
        ArgumentCaptor<Venue> venueArgumentCaptor =
                ArgumentCaptor.forClass(Venue.class);

        // Act
        // Mock the userRepository behavior
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(existingVenue));
        when(venueRepository.save(any(Venue.class))).thenReturn(newVenue);
        Venue result = underTest.updateVenue(venueId, newVenue.getVenueName(), newVenue.getVenueLocation());

        // Assert
        assertNotNull(result);
        assertThat(result).isEqualTo(newVenue);

        verify(venueRepository).save(venueArgumentCaptor.capture());
        Venue updatedVenue = venueArgumentCaptor.getValue();
        assertEquals(updatedVenue, newVenue);
    }

    @Test
    public void testUpdateVenueWhenVenueDoesNotExists() {
        // Arrange
        Integer venueId = -1;
        Venue nonExistentVenue = Venue.builder()
                .venueId(venueId)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();

        // Mock the userRepository behavior to return an empty Optional, indicating that the venue doesn't exist
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class,
                () -> underTest.updateVenue(venueId, nonExistentVenue.getVenueName(), nonExistentVenue.getVenueLocation()));
    }

    @Test
    public void testUpdateVenueWhenVenueNameAlreadyExist() {
        // Arrange
        Integer venueId = 99;
        Integer altVenueId = 1;
        String venueName = "ExistingVenue";
        String venueLocation = "NewLocation";

        Venue existingVenue = Venue.builder()
                .venueId(venueId)
                .venueName(venueName)
                .venueLocation(venueLocation)
                .venueImage(null)
                .build();

        Venue newVenue = Venue.builder()
                .venueId(altVenueId)
                .venueName(venueName)
                .venueLocation("New Venue Location")
                .venueImage(null)
                .build();

        when(venueRepository.findById(altVenueId)).thenReturn(Optional.of(newVenue));
        when(venueRepository.findByVenueName(venueName)).thenReturn(Optional.of(existingVenue));


        // Act and Assert
        assertThrows(AlreadyExistsException.class,
                () -> underTest.updateVenue(altVenueId, newVenue.getVenueName(), newVenue.getVenueLocation()));
    }

    @Test
    public void testUpdateVenueWithEmptyLocationAndNoNewName() {
        // Arrange
        Integer venueId = 99;
        String venueName = "ExistingVenue";

        Venue existingVenue = Venue.builder()
                .venueId(venueId)
                .venueName(venueName)
                .venueLocation(null)// empty location
                .venueImage(null)
                .build();

        when(venueRepository.findById(venueId)).thenReturn(Optional.of(existingVenue));

        // Act
        when(venueRepository.save(any(Venue.class))).thenReturn(existingVenue);
        Venue result = underTest.updateVenue(venueId, venueName, null); // No new name and no new location

        // Assert
        assertNotNull(result);
        assertEquals(venueId, result.getVenueId());
        assertEquals(venueName, result.getVenueName()); // Name should remain unchanged
        assertNull(result.getVenueLocation()); // Location should be null

        // Verify that repository methods were called
        verify(venueRepository).findById(venueId);
        verify(venueRepository).findByVenueName(anyString());
        verify(venueRepository).save(existingVenue); // Ensure save is called with the existing venue
    }

    @Test
    public void testUpdateVenueWithNoNewLocationAndEmptyName() {
        // Arrange
        Integer venueId = 99;
        String venueLocation = "ExistingVenueLocation";

        Venue existingVenue = Venue.builder()
                .venueId(venueId)
                .venueName(null)// empty name
                .venueLocation(venueLocation)
                .venueImage(null)
                .build();


        when(venueRepository.findById(venueId)).thenReturn(Optional.of(existingVenue));

        // Act
        when(venueRepository.save(any(Venue.class))).thenReturn(existingVenue);
        Venue result = underTest.updateVenue(venueId, null, venueLocation); // Empty new name and no new location

        // Assert
        assertNotNull(result);
        assertEquals(venueId, result.getVenueId());
        assertEquals(venueLocation, result.getVenueLocation()); // location should remain unchanged
        assertNull(result.getVenueName()); // Location should be null

        // Verify that repository methods were called
        verify(venueRepository).findById(venueId);
        verify(venueRepository, never()).findByVenueName(anyString());
        verify(venueRepository).save(existingVenue); // Ensure save is called with the existing venue
    }

    @Test
    public void testRemoveVenueWhenVenueExist() {
        // Arrange
        Integer venueId = 99;
        Venue venue = Venue.builder()
                .venueId(venueId)
                .venueName("testPlace")
                .venueLocation("testPlaceLocation")
                .venueImage(null)
                .build();
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));

        // Act
        underTest.removeVenue(venueId);

        // Assert
        verify(venueRepository).deleteById(venueId);
    }

    @Test
    public void testRemoveVenueWhenVenueDoesNotExist() {
        // Arrange
        Integer venueId = -1;
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> underTest.removeVenue(venueId));
    }
}