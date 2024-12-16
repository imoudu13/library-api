package com.moducation.library.api.service;

import com.moducation.library.api.exceptions.IncorrectFilterException;
import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.repositories.BookActivityHistoryRepository;
import com.moducation.library.api.repositories.BookRepository;
import com.moducation.library.api.repositories.BookWithdrawalRepository;
import com.moducation.library.api.utils.Constants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookActivityHistoryRepository bookActivityHistoryRepository;
    private final BookWithdrawalRepository bookWithdrawalRepository;

    @Autowired
    public BookService(BookRepository bookRepository, BookActivityHistoryRepository bookActivityHistoryRepository, BookWithdrawalRepository bookWithdrawalRepository) {
        this.bookWithdrawalRepository = bookWithdrawalRepository;
        this.bookRepository = bookRepository;
        this.bookActivityHistoryRepository = bookActivityHistoryRepository;
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> findBooksByFilter(String filter, String key) throws IncorrectFilterException {
        return switch (filter) {
            case "title" -> bookRepository.findByTitle(key);
            case "author" -> bookRepository.findByAuthor(key);
            case "genre" -> bookRepository.findByGenre(key);
            default -> throw new IncorrectFilterException("That filter doesn't exist.");
        };
    }

    public Book updateBook(Book book) {
        return bookRepository.updateBook(book.getTitle(), book.getAuthor(), book.getGenre(), book.getAvailability(), book.getId());
    }

    public boolean checkIfBookIsAvailable(long bookId) {
        Integer bookAvailability = bookRepository.getAvailability(bookId);

        return bookAvailability != null && bookAvailability > 0;
    }

    @Transactional
    public void borrowBook(long bookId) {
        Integer bookAvailability = bookRepository.getAvailability(bookId);

        bookRepository.updateAvailability(--bookAvailability, bookId);
    }

    public BookActivityHistory newActivity(Book book, LibraryUser user, Integer type) {
        BookActivityHistory bookActivityHistory = BookActivityHistory.builder()
                .libraryUser(user)
                .book(book)
                .type(type).build();

        return bookActivityHistoryRepository.save(bookActivityHistory);
    }

    public BookWithdrawal newWithdrawal(BookActivityHistory bookActivity, LibraryUser user) {
        long expectedReturnDateInMillis = System.currentTimeMillis() + Constants.ONE_WEEK_IN_MILLIS;
        Date expectedReturnDate = new Date(expectedReturnDateInMillis);

        BookWithdrawal bookWithdrawal = BookWithdrawal.builder()
                .libraryUser(user)
                .bookActivity(bookActivity)
                .expectedReturnDate(expectedReturnDate).build();

        return bookWithdrawalRepository.save(bookWithdrawal);
    }
}
