package com.moducation.library.api.service;

import com.moducation.library.api.exceptions.IncorrectFilterException;
import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.BookReturn;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.repositories.BookActivityHistoryRepository;
import com.moducation.library.api.repositories.BookRepository;
import com.moducation.library.api.repositories.BookReturnRepository;
import com.moducation.library.api.repositories.BookWithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.persistence.EntityManager;
import org.mockito.InjectMocks;
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

    @Mock
    private EntityManager entityManager;

    @Mock
    private BookReturnRepository bookReturnRepository;

    @InjectMocks
    private BookService bookService;
    private Book book1;
    private Book book2;
    private Book book3;

    private LibraryUser libraryUser;
    private BookActivityHistory bookActivityHistory;
    private BookWithdrawal bookWithdrawal;

    @BeforeEach
    public void setUp() {
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
    public void testBookIsAvailable_Success() {
        when(bookRepository.getAvailability(1L)).thenReturn(1);

        boolean bookAvailability = bookService.checkIfBookIsAvailable(1L);

        assertTrue(bookAvailability);
    }

    @Test
    public void testBookIsAvailable_Fail() {
        when(bookRepository.getAvailability(1L)).thenReturn(null);

        boolean bookAvailability = bookService.checkIfBookIsAvailable(1L);

        assertFalse(bookAvailability);
    }

    @Test
    public void testBookIsAvailable_Fail_Zero() {
        when(bookRepository.getAvailability(1L)).thenReturn(0);

        boolean bookAvailability = bookService.checkIfBookIsAvailable(1L);

        assertFalse(bookAvailability);
    }

    @Test
    public void testBorrowBook() {
        when(bookRepository.getAvailability(1L)).thenReturn(1);

        bookService.borrowBook(1L);

        verify(bookRepository).updateAvailability(0, 1L);
    }

    @Test
    public void testNewActivity() {
        when(entityManager.merge(libraryUser)).thenReturn(libraryUser);  // Return the same user instance
        when(entityManager.merge(book1)).thenReturn(book1);
        when(bookActivityHistoryRepository.save(any())).thenReturn(bookActivityHistory);

        BookActivityHistory result = bookService.newActivity(book1, libraryUser, 1);

        assertEquals(bookActivityHistory.getId(), result.getId());
        verify(bookActivityHistoryRepository).save(any());
    }

    @Test
    public void testNewWithdrawal() {
        when(entityManager.merge(libraryUser)).thenReturn(libraryUser);  // Return the same user instance
        when(entityManager.merge(bookActivityHistory)).thenReturn(bookActivityHistory);
        when(bookWithdrawalRepository.save(any())).thenReturn(bookWithdrawal);

        BookWithdrawal result = bookService.newWithdrawal(bookActivityHistory, libraryUser);
        assertEquals(result.getId(), bookWithdrawal.getId());
    }

    @Test
    public void testReturnBook() {
        bookService.returnBook(1L);

        verify(bookRepository).updateAvailability(1, 1L);
    }

    @Test
    public void testNewReturn() {
        when(entityManager.merge(libraryUser)).thenReturn(libraryUser);  // Return the same user instance
        when(entityManager.merge(book1)).thenReturn(book1);

        BookReturn bookReturn = BookReturn.builder()
                .id(1L)
                .libraryUser(libraryUser)
                .book(book1)
                .bookActivityHistory(bookActivityHistory)
                .bookWithdrawal(bookWithdrawal).build();

        when(bookReturnRepository.save(any())).thenReturn(bookReturn);

        BookReturn result = bookService.newReturn(bookActivityHistory, libraryUser, book1, bookWithdrawal);

        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetBookActivityHistory() {
        BookActivityHistory bookActivityHistory = BookActivityHistory.builder().id(1L).build();

        when(bookActivityHistoryRepository.findBookActivityForWithdrawal(1, libraryUser, book1)).thenReturn(bookActivityHistory);

        BookActivityHistory result = bookService.getBookActivityHistory(1, libraryUser, book1);

        assertEquals(bookActivityHistory.getId(), result.getId());
    }

    @Test
    public void testGetBookWithdrawal() {
        BookWithdrawal bookWithdrawal = BookWithdrawal.builder().id(1L).build();

        when(bookWithdrawalRepository.findByBookActivityHistoryAndLibraryUser(bookActivityHistory, libraryUser)).thenReturn(bookWithdrawal);

        BookWithdrawal result = bookService.getBookWithdrawal(bookActivityHistory, libraryUser);

        assertEquals(bookWithdrawal.getId(), result.getId());
    }
}
