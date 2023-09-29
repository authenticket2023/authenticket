package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDtoMapper;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketCategoryServiceImplTest {

    @Mock
    private TicketCategoryRepository ticketCategoryRepository;

    @InjectMocks
    private TicketCategoryDisplayDtoMapper ticketCategoryDisplayDtoMapper;

    private TicketCategoryServiceImpl underTest;

    @BeforeEach
    void setUp(){
        underTest = new TicketCategoryServiceImpl(ticketCategoryRepository, ticketCategoryDisplayDtoMapper);
    }

    @Test
    public void testFindAllTicketCategory() {
        // Arrange: Mock data
        List<TicketCategory> categories = Arrays.asList(
                new TicketCategory(1, "Category1"),
                new TicketCategory(2, "Category2")
        );

        // Mock repository behavior
        when(ticketCategoryRepository.findAll()).thenReturn(categories);

        //Act: Call the method being tested
        List<TicketCategoryDisplayDto> result = underTest.findAllTicketCategory();

        // Assertions
        verify(ticketCategoryRepository).findAll();
        assertEquals(categories.size(), result.size());
    }

    @Test
    public void testFindTicketCategoryById() {
        // Arrange
        int categoryId = 1;
        TicketCategory category = new TicketCategory(categoryId, "CategoryName");
        // Mock the repo behavior
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act: Find by given id
        Optional<TicketCategoryDisplayDto> result = underTest.findTicketCategoryById(categoryId);

        // Assertions
        assertTrue(result.isPresent());
        assertEquals(categoryId, result.get().categoryId());
    }

    @Test
    public void testSaveTicketCategory() {
        // Arrange
        String categoryName = "NewCategory";
        when(ticketCategoryRepository.findByCategoryName(categoryName)).thenReturn(Optional.empty());
        when(ticketCategoryRepository.save(any())).thenReturn(new TicketCategory());

        // Act
        TicketCategory result = underTest.saveTicketCategory(categoryName);

        // Assertions
        assertNotNull(result);
    }

    @Test
    public void testSaveTicketCategoryAlreadyExists() {
        // Arrange
        String categoryName = "ExistingCategory";
        when(ticketCategoryRepository.findByCategoryName(categoryName)).thenReturn(Optional.of(new TicketCategory()));

        // Act and Assert
        assertThrows(AlreadyExistsException.class, () -> underTest.saveTicketCategory(categoryName));
    }

    @Test
    public void testUpdateTicketCategory() {
        // Arrange
        int categoryId = 1;
        String newCategoryName = "UpdatedCategory";
        TicketCategoryUpdateDto updateDto = new TicketCategoryUpdateDto(categoryId, newCategoryName);
        TicketCategory existingCategory = new TicketCategory(categoryId, "ExistingCategory");

        when(ticketCategoryRepository.findByCategoryName(newCategoryName)).thenReturn(Optional.empty());
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        // Act
        TicketCategory result = underTest.updateTicketCategory(categoryId, newCategoryName);

        // Assertions
        assertNotNull(result);
        assertEquals(newCategoryName, result.getCategoryName());
    }

    @Test
    public void testUpdateTicketCategoryAlreadyExists() {
        // Arrange
        int categoryId = 1;
        String newCategoryName = "ExistingCategory"; // Name that already exists
        TicketCategoryUpdateDto updateDto = new TicketCategoryUpdateDto(categoryId, newCategoryName);

        when(ticketCategoryRepository.findByCategoryName(newCategoryName)).thenReturn(Optional.of(new TicketCategory()));

        // Act and Assert
        assertThrows(AlreadyExistsException.class, () -> underTest.updateTicketCategory(categoryId, newCategoryName));
    }

    @Test
    public void testUpdateTicketCategoryNonExistent() {
        // Arrange
        int categoryId = -1;
        String newCategoryName = "UpdatedCategory";

        // Mock the repo behaviour to return an empty optional
        when(ticketCategoryRepository.findByCategoryName(newCategoryName)).thenReturn(Optional.empty());
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> underTest.updateTicketCategory(categoryId, newCategoryName));
    }

    @Test
    public void testDeleteTicket() {
        // Arrange
        int categoryId = 1;
        TicketCategory category = new TicketCategory(categoryId, "CategoryName");

        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        underTest.deleteTicket(categoryId);

        // Assertions
        assertNotNull(category.getDeletedAt());
        verify(ticketCategoryRepository).save(category);
    }

    @Test
    public void testDeleteTicketAlreadyDeleted() {
        // Arrange
        int categoryId = 1;
        TicketCategory category = new TicketCategory(categoryId, "CategoryName");
        category.setDeletedAt(LocalDateTime.now());
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act and Assert
        assertThrows(AlreadyDeletedException.class, () -> underTest.deleteTicket(categoryId));
    }

    @Test
    public void testDeleteTicketNonExistent() {
        // Arrange
        int categoryId = 1;
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> underTest.deleteTicket(categoryId));
    }

    @Test
    public void testRemoveTicketCategory() {
        // Arrange
        int categoryId = 1;
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.of(new TicketCategory()));

        // Act and Assert
        assertDoesNotThrow(() -> underTest.removeTicketCategory(categoryId));
    }

    @Test
    public void testRemoveTicketCategoryNonExistent() {
        // Arrange
        int categoryId = 1;
        when(ticketCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NonExistentException.class, () -> underTest.removeTicketCategory(categoryId));
    }
}