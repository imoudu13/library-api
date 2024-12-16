package com.moducation.library.api.controller;

import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.service.BookService;
import com.moducation.library.api.service.UserService;

import java.util.Arrays;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;


class BookManagementControllerTest {

    private BookService bookService;
    private UserService userService;

    private BookManagementController bookController;

    @BeforeEach
    void setup() {
        bookService = mock(BookService.class);
        userService = mock(UserService.class);
        bookController = new BookManagementController(bookService, userService);
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
        assertEquals(200, response.getStatusCode().value());
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
        assertEquals(200, response.getStatusCode().value());
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
        assertEquals(201, response.getStatusCode().value());
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
        assertEquals(200, response.getStatusCode().value());
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
        assertEquals(404, response.getStatusCode().value());
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
        assertEquals(500, response.getStatusCode().value());
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
        assertEquals(500, response.getStatusCode().value());
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
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error saving book", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).save(book);
    }

    @Test
    void testUpdateBook() {
        Book book = Book.builder().id(1L).title("Book").author("Author").build();
        when(bookService.findById(1L)).thenReturn(book);

        ResponseEntity<Object> response = bookController.updateBook(book);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateBook_NotFound() {
        Book book = Book.builder().id(1L).title("Book").author("Author").build();
        when(bookService.findById(1L)).thenReturn(null);

        ResponseEntity<Object> response = bookController.updateBook(book);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Book not found.", response.getBody());
    }

    @Test
    void testUpdateBook_Exception() {
        Book book = Book.builder().id(1L).title("Book").author("Author").build();
        when(bookService.findById(1L)).thenThrow(new RuntimeException("Error fetching book"));

        ResponseEntity<Object> response = bookController.updateBook(book);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error fetching book", response.getBody());
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
        assertEquals(500, response.getStatusCode().value());
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
        assertEquals(404, response.getStatusCode().value());
        assertEquals("There is no book with that Id.", response.getBody());

        // Verify service interaction
        verify(bookService, times(1)).findById(999L);
        verify(bookService, never()).delete(anyLong());
    }

    @Test
    void testBorrowBook_Success() {
        // Arrange: Mock book, session, and user
        Book book = Book.builder().id(1L).title("Test Book").build();
        LibraryUser user = LibraryUser.builder().username("testUser").firstname("Test USer").build();

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookService.checkIfBookIsAvailable(1L)).thenReturn(true);

        BookActivityHistory activityHistory = BookActivityHistory.builder().type(1).book(book).libraryUser(user).build();
        when(bookService.newActivity(book, user, 1)).thenReturn(activityHistory);

        BookWithdrawal withdrawal = BookWithdrawal.builder().id(1L).bookActivity(activityHistory).libraryUser(user).build();
        when(bookService.newWithdrawal(activityHistory, user)).thenReturn(withdrawal);

        // Act: Call the controller method
        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        // Assert: Validate response and interactions
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(withdrawal, response.getBody());

        verify(bookService, times(1)).checkIfBookIsAvailable(1L);
        verify(bookService, times(1)).borrowBook(1L);
        verify(bookService, times(1)).newActivity(book, user, 1);
        verify(bookService, times(1)).newWithdrawal(activityHistory, user);
    }

    @Test
    void testBorrowBook_BookNotAvailable() {
        // Arrange: Mock book and session
        Book book = Book.builder().id(1L).title("Test Book").build();
        HttpSession session = mock(HttpSession.class);
        LibraryUser user = LibraryUser.builder().username("testUser").firstname("Test USer").build();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookService.checkIfBookIsAvailable(1L)).thenReturn(false);

        // Act: Call the controller method
        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        // Assert: Validate response and interactions
        assertEquals(400, response.getStatusCode().value());
        assertEquals("book is not available.", response.getBody());

        verify(bookService, times(1)).checkIfBookIsAvailable(1L);
        verify(bookService, never()).borrowBook(anyLong());
        verify(bookService, never()).newActivity(book, user, 1);
        verify(bookService, never()).newWithdrawal(any(), any());
    }

    @Test
    void testBorrowBook_NoUserInSession() {
        // Arrange: Mock book and session with no user
        Book book = Book.builder().id(1L).title("Test Book").build();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(null);

        // Act: Call the controller method
        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        // Assert: Validate response and interactions
        assertEquals(401, response.getStatusCode().value());
        assertEquals("please login", response.getBody()); // Assuming session.getAttribute("user") returns null

        verify(bookService, never()).checkIfBookIsAvailable(anyLong());
        verify(bookService, never()).borrowBook(anyLong());
        verify(bookService, never()).newActivity(any(), any(), anyInt());
        verify(bookService, never()).newWithdrawal(any(), any());
    }

    @Test
    void testBorrowBook_Exception() {
        // Arrange: Mock book and session
        Book book = Book.builder().id(1L).title("Test Book").build();
        LibraryUser user = LibraryUser.builder().username("testUser").firstname("Test USer").build();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        // Simulate exception
        when(bookService.checkIfBookIsAvailable(1L)).thenThrow(new RuntimeException("Unexpected error"));

        // Act: Call the controller method
        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        // Assert: Validate response and interactions
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected error", response.getBody());

        verify(bookService, times(1)).checkIfBookIsAvailable(1L);
        verify(bookService, never()).borrowBook(anyLong());
        verify(bookService, never()).newActivity(any(), any(), anyInt());
        verify(bookService, never()).newWithdrawal(any(), any());
    }
}