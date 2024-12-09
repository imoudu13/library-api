package com.moducation.library.api.controller;

import com.moducation.library.api.models.Book;
import com.moducation.library.api.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;


class BookManagementControllerTest {

    private BookService bookService;
    private BookManagementController bookController;

    @BeforeEach
    void setup() {
        bookService = mock(BookService.class);
        bookController = new BookManagementController(bookService);
    }

    @Test
    void testGetBooks() {
        // Mock the service response
        when(bookService.findAll()).thenReturn(Arrays.asList(
                Book.builder()
                        .id(1L)
                        .title("Book 1")
                        .author("Author 1")
                        .genre("Fiction")
                        .availability(10)
                        .avgRating(4.5F)
                        .build(),
                Book.builder()
                        .id(2L)
                        .title("Book 2")
                        .author("Author 2")
                        .genre("Non-Fiction")
                        .availability(5)
                        .avgRating(4.0F)
                        .build()
        ));

        // Call the controller method
        ResponseEntity<Object> response = bookController.getBooks();

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
        // Mock the service response
        when(bookService.findById(1L)).thenReturn(
                Book.builder()
                        .id(1L)
                        .title("Book 1")
                        .author("Author 1")
                        .genre("Fiction")
                        .availability(10)
                        .avgRating(4.5F)
                        .build()
        );

        // Call the controller method
        ResponseEntity<Object> response = bookController.getBookById(1L);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(1L);
    }

    @Test
    void testAddBook() {
        // Mock the service response
        Book newBook = Book.builder()
                .title("New Book")
                .author("New Author")
                .genre("Mystery")
                .availability(20)
                .build();
        Book savedBook = Book.builder()
                .id(3L)
                .title("New Book")
                .author("New Author")
                .genre("Mystery")
                .availability(20)
                .build();

        when(bookService.save(newBook)).thenReturn(savedBook);

        // Call the controller method
        ResponseEntity<Object> response = bookController.addBook(newBook);

        // Assertions
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).save(newBook);
    }

    @Test
    void testDeleteBookById() {
        // Mock the service response
        Book existingBook = Book.builder()
                .id(1L)
                .title("Book to Delete")
                .author("Author")
                .genre("Thriller")
                .availability(5)
                .build();
        when(bookService.findById(1L)).thenReturn(existingBook);
        doNothing().when(bookService).delete(1L);

        // Call the controller method
        ResponseEntity<Object> response = bookController.deleteBookById(1L);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(1L);
        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void testDeleteBookById_NotFound() {
        // Mock the service response for a non-existent book
        when(bookService.findById(999L)).thenReturn(null);

        // Call the controller method
        ResponseEntity<Object> response = bookController.deleteBookById(999L);

        // Assertions
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("There is no book with that Id.", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(999L);
        verify(bookService, never()).delete(anyLong());
    }

    @Test
    void testGetBooks_Exception() {
        // Mock the service to throw an exception
        when(bookService.findAll()).thenThrow(new RuntimeException("Database unavailable"));

        // Call the controller method
        ResponseEntity<Object> response = bookController.getBooks();

        // Assertions
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Database unavailable", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findAll();
    }

    @Test
    void testGetBookById_Exception() {
        // Mock the service to throw an exception
        when(bookService.findById(1L)).thenThrow(new RuntimeException("Error fetching book"));

        // Call the controller method
        ResponseEntity<Object> response = bookController.getBookById(1L);

        // Assertions
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error fetching book", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(1L);
    }

    @Test
    void testAddBook_Exception() {
        // Mock the service to throw an exception
        Book book = Book.builder().title("Book").author("Author").build();
        when(bookService.save(book)).thenThrow(new RuntimeException("Error saving book"));

        // Call the controller method
        ResponseEntity<Object> response = bookController.addBook(book);

        // Assertions
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error saving book", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).save(book);
    }

    @Test
    void testDeleteBookById_Exception() {
        // Mock the service to throw an exception
        when(bookService.findById(1L)).thenReturn(
                Book.builder().id(1L).title("Book to Delete").author("Author").build()
        );
        doThrow(new RuntimeException("Error deleting book")).when(bookService).delete(1L);

        // Call the controller method
        ResponseEntity<Object> response = bookController.deleteBookById(1L);

        // Assertions
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error deleting book", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(1L);
        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void testDeleteBookById_NotFound_Exception() {
        // Mock the service to throw an exception when book is not found
        when(bookService.findById(999L)).thenReturn(null);

        // Call the controller method
        ResponseEntity<Object> response = bookController.deleteBookById(999L);

        // Assertions
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("There is no book with that Id.", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(999L);
        verify(bookService, never()).delete(anyLong());
    }
}