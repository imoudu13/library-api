package com.moducation.library.api.controller;

import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookReturn;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.service.BookService;
import com.moducation.library.api.service.UserService;

import java.util.Arrays;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static com.moducation.library.api.utils.Constants.RETURN_CODE;
import static com.moducation.library.api.utils.Constants.WITHDRAWAL_CODE;
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
import static org.springframework.http.HttpStatus.*;


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

        ResponseEntity<Object> response = bookController.getBooks();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        verify(bookService, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
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

        ResponseEntity<Object> response = bookController.getBookById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        verify(bookService, times(1)).findById(1L);
    }

    @Test
    void testAddBook() {
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

        ResponseEntity<Object> response = bookController.addBook(newBook);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());

        verify(bookService, times(1)).save(newBook);
    }

    @Test
    void testDeleteBookById() {
        Book existingBook = Book.builder()
                .id(1L)
                .title("Book to Delete")
                .author("Author")
                .genre("Thriller")
                .availability(5)
                .build();
        when(bookService.findById(1L)).thenReturn(existingBook);
        doNothing().when(bookService).delete(1L);

        ResponseEntity<Object> response = bookController.deleteBookById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        verify(bookService, times(1)).findById(1L);
        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void testDeleteBookById_NotFound() {
        when(bookService.findById(999L)).thenReturn(null);

        ResponseEntity<Object> response = bookController.deleteBookById(999L);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("There is no book with that Id.", response.getBody());

        verify(bookService, times(1)).findById(999L);
        verify(bookService, never()).delete(anyLong());
    }

    @Test
    void testGetBooks_Exception() {
        when(bookService.findAll()).thenThrow(new RuntimeException("Database unavailable"));

        ResponseEntity<Object> response = bookController.getBooks();

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Database unavailable", response.getBody());

        verify(bookService, times(1)).findAll();
    }

    @Test
    void testGetBookById_Exception() {
        when(bookService.findById(1L)).thenThrow(new RuntimeException("Error fetching book"));

        ResponseEntity<Object> response = bookController.getBookById(1L);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error fetching book", response.getBody());

        verify(bookService, times(1)).findById(1L);
    }

    @Test
    void testAddBook_Exception() {
        Book book = Book.builder().title("Book").author("Author").build();
        when(bookService.save(book)).thenThrow(new RuntimeException("Error saving book"));

        ResponseEntity<Object> response = bookController.addBook(book);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error saving book", response.getBody());

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
        when(bookService.findById(1L)).thenReturn(
                Book.builder().id(1L).title("Book to Delete").author("Author").build()
        );
        doThrow(new RuntimeException("Error deleting book")).when(bookService).delete(1L);

        ResponseEntity<Object> response = bookController.deleteBookById(1L);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error deleting book", response.getBody());

        verify(bookService, times(1)).findById(1L);
        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void testDeleteBookById_NotFound_Exception() {
        when(bookService.findById(999L)).thenReturn(null);

        ResponseEntity<Object> response = bookController.deleteBookById(999L);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("There is no book with that Id.", response.getBody());

        verify(bookService, times(1)).findById(999L);
        verify(bookService, never()).delete(anyLong());
    }

    @Test
    void testBorrowBook_Success() {
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

        ResponseEntity<Object> response = bookController.borrowBook(book, session);

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
        Book book = Book.builder().id(1L).title("Test Book").build();
        HttpSession session = mock(HttpSession.class);
        LibraryUser user = LibraryUser.builder().username("testUser").firstname("Test USer").build();
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookService.checkIfBookIsAvailable(1L)).thenReturn(false);

        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("book is not available.", response.getBody());

        verify(bookService, times(1)).checkIfBookIsAvailable(1L);
        verify(bookService, never()).borrowBook(anyLong());
        verify(bookService, never()).newActivity(book, user, 1);
        verify(bookService, never()).newWithdrawal(any(), any());
    }

    @Test
    void testBorrowBook_NoUserInSession() {
        Book book = Book.builder().id(1L).title("Test Book").build();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("please login", response.getBody()); // Assuming session.getAttribute("user") returns null

        verify(bookService, never()).checkIfBookIsAvailable(anyLong());
        verify(bookService, never()).borrowBook(anyLong());
        verify(bookService, never()).newActivity(any(), any(), anyInt());
        verify(bookService, never()).newWithdrawal(any(), any());
    }

    @Test
    void testBorrowBook_Exception() {
        Book book = Book.builder().id(1L).title("Test Book").build();
        LibraryUser user = LibraryUser.builder().username("testUser").firstname("Test USer").build();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        when(bookService.checkIfBookIsAvailable(1L)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Object> response = bookController.borrowBook(book, session);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Unexpected error", response.getBody());

        verify(bookService, times(1)).checkIfBookIsAvailable(1L);
        verify(bookService, never()).borrowBook(anyLong());
        verify(bookService, never()).newActivity(any(), any(), anyInt());
        verify(bookService, never()).newWithdrawal(any(), any());
    }
    @Test
    public void testReturnBook_Success() {
        Long userId = 1L;
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(userId);

        Book book = new Book();
        book.setId(1L);

        LibraryUser user = new LibraryUser();
        user.setId(userId);

        BookActivityHistory activityHistory = new BookActivityHistory();
        BookActivityHistory withdrawalActivity = new BookActivityHistory();
        BookWithdrawal withdrawal = new BookWithdrawal();
        BookReturn bookReturn = new BookReturn();

        when(userService.getUserById(userId)).thenReturn(user);
        doNothing().when(bookService).returnBook(book.getId());
        when(bookService.newActivity(book, user, RETURN_CODE)).thenReturn(activityHistory);
        when(bookService.getBookActivityHistory(WITHDRAWAL_CODE, user, book)).thenReturn(withdrawalActivity);
        when(bookService.getBookWithdrawal(withdrawalActivity, user)).thenReturn(withdrawal);
        when(bookService.newReturn(activityHistory, user, book, withdrawal)).thenReturn(bookReturn);

        ResponseEntity<Object> response = bookController.returnBook(book, session);

        assertEquals(OK, response.getStatusCode());
        assertEquals(bookReturn, response.getBody());
    }

    @Test
    public void testReturnBook_UserIdNull() {
        Book book = new Book();
        HttpSession session = mock(HttpSession.class);

        ResponseEntity<Object> response = bookController.returnBook(book, session);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("user id is null.", response.getBody());
    }

    @Test
    public void testReturnBook_UserNotFound() {
        Long userId = 1L;
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(1L);

        Book book = new Book();
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<Object> response = bookController.returnBook(book, session);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    public void testReturnBook_ExceptionThrown() {
        Long userId = 1L;
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userId")).thenReturn(1L);

        Book book = new Book();
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Object> response = bookController.returnBook(book, session);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }
}
