package com.moducation.library.api.service;

import com.moducation.library.api.exceptions.IncorrectFilterException;
import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.repositories.BookActivityHistoryRepository;
import com.moducation.library.api.repositories.BookRepository;
import com.moducation.library.api.repositories.BookWithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookActivityHistoryRepository bookActivityHistoryRepository;

    @Mock
    private BookWithdrawalRepository bookWithdrawalRepository;

    private BookService bookService;
    private Book book1;
    private Book book2;
    private Book book3;

    private LibraryUser libraryUser;
    private BookActivityHistory bookActivityHistory;
    private BookWithdrawal bookWithdrawal;

    @BeforeEach
    public void setUp() {
        bookService = new BookService(
                bookRepository,
                bookActivityHistoryRepository,
                bookWithdrawalRepository);
        book1 = new Book(1L, "title", "author", "genre", 7, 3.0f, 30f, 10f);
        book2 = new Book(2L, "title2", "author2", "genre", 9, 3.8f, 38f, 10f);
        book3 = new Book(3L, "not", "author3", "genre", 9, 3.8f, 38f, 10f);
        bookWithdrawal = BookWithdrawal.builder().id(1L).build();
        libraryUser = LibraryUser.builder().id(1L).build();
        bookActivityHistory = BookActivityHistory.builder().id(1L).build();
    }

    @Test
    public void testSave() {
        when(bookRepository.save(book1)).thenReturn(book1);

        Book book = bookService.save(book1);

        verify(bookRepository).save(book1);
        assertEquals(book1.getId(), book.getId());
    }

    @Test
    public void testFindByIdSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(book1));

        Book result = bookService.findById(1L);

        verify(bookRepository).findById(1L);
        assertEquals(book1.getId(), result.getId());
    }

    @Test
    public void testFindByIdFail() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Book result = bookService.findById(1L);

        verify(bookRepository).findById(1L);
        assertNull(result);
    }

    @Test
    public void testFindAll() {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));

        List<Book> books = bookService.findAll();

        assertEquals(3, books.size());
        assertEquals(1L, books.getFirst().getId());
        assertEquals("title", books.getFirst().getTitle());
        assertEquals("author", books.getFirst().getAuthor());
        assertEquals("genre", books.getFirst().getGenre());
    }

    @Test
    public void delete() {
        bookService.delete(book1.getId());
        verify(bookRepository).deleteById(1L);
    }

    @Test
    public void testFindBooksByFilterTitle() throws IncorrectFilterException {
        when(bookRepository.findByTitle("title")).thenReturn(List.of(book1, book2));

        List<Book> books = bookService.findBooksByFilter("title", "title");

        verify(bookRepository).findByTitle("title");
        assertEquals(2, books.size());
        assertEquals(1L, books.getFirst().getId());
        assertEquals("title", books.getFirst().getTitle());
        assertEquals("author", books.getFirst().getAuthor());
        assertEquals("genre", books.getFirst().getGenre());
    }

    @Test
    public void testFindBooksByFilterAuthor() throws IncorrectFilterException {
        when(bookRepository.findByAuthor("author")).thenReturn(List.of(book1, book2, book3));

        List<Book> books = bookService.findBooksByFilter("author", "author");

        verify(bookRepository).findByAuthor("author");
        assertEquals(3, books.size());
        assertEquals(1L, books.getFirst().getId());
        assertEquals("title", books.getFirst().getTitle());
        assertEquals("author", books.getFirst().getAuthor());
        assertEquals("genre", books.getFirst().getGenre());
    }

    @Test
    public void testFindBooksByFilterGenre() throws IncorrectFilterException {
        when(bookRepository.findByGenre("genre")).thenReturn(List.of(book1, book2, book3));

        List<Book> books = bookService.findBooksByFilter("genre", "genre");

        verify(bookRepository).findByGenre("genre");
        assertEquals(3, books.size());
        assertEquals(1L, books.getFirst().getId());
        assertEquals("title", books.getFirst().getTitle());
        assertEquals("author", books.getFirst().getAuthor());
        assertEquals("genre", books.getFirst().getGenre());
    }

    @Test
    public void testFindBooksByFilterFail() {
        assertThrows(IncorrectFilterException.class, () -> bookService.findBooksByFilter("not", "genre"));
    }

    @Test
    public void testUpdateBook() {
        when(bookRepository.updateBook(book1.getTitle(), book1.getAuthor(), book1.getGenre(), book1.getAvailability(), book1.getId())).thenReturn(book1);

        Book result = bookService.updateBook(book1);
        assertEquals("title", result.getTitle());
        assertEquals("author", result.getAuthor());
        assertEquals("genre", result.getGenre());
        assertEquals(7, result.getAvailability());
    }

    @Test
    public void testBookIsAvailable() {
        when(bookRepository.getAvailability(1L)).thenReturn(1);

        boolean bookAvailability = bookService.checkIfBookIsAvailable(1L);

        assertTrue(bookAvailability);
    }

    @Test
    public void testBorrowBook() {
        when(bookRepository.getAvailability(1L)).thenReturn(1);

        bookService.borrowBook(1L);

        verify(bookRepository).updateAvailability(0, 1L);
    }

    @Test
    public void testNewActivity() {
        when(bookActivityHistoryRepository.save(any())).thenReturn(bookActivityHistory);

        BookActivityHistory result = bookService.newActivity(book1, libraryUser, 1);

        assertEquals(bookActivityHistory.getId(), result.getId());
        verify(bookActivityHistoryRepository).save(any());
    }

    @Test
    public void testNewWithdrawal() {
        when(bookWithdrawalRepository.save(any())).thenReturn(bookWithdrawal);

        BookWithdrawal result = bookService.newWithdrawal(bookActivityHistory, libraryUser);
        assertEquals(result.getId(), bookWithdrawal.getId());
    }
}
