package com.moducation.library.api.controller;

import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.service.BookService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/books")
public class BookManagementController {
    private final BookService bookService;

    @Autowired
    public BookManagementController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/get-books")
    public ResponseEntity<Object> getBooks() {
        try {
            return new ResponseEntity<>(bookService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addBook(@RequestBody Book book) {
        try {
            return new ResponseEntity<>(bookService.save(book), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Object> updateBook(@RequestBody Book book) {
        try {
            Book resultBook = bookService.findById(book.getId());

            if (resultBook == null) {
                return new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(bookService.updateBook(book), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteBookById(@PathVariable Long id) {
        try {
            Book book = bookService.findById(id);
            if (book == null) {
                return new ResponseEntity<>("There is no book with that Id.", HttpStatus.NOT_FOUND);
            }

            bookService.delete(id);

            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/borrow")
    public ResponseEntity<Object> borrowBook(@RequestBody Book book, HttpSession session) {
        try {
            LibraryUser user = (LibraryUser) session.getAttribute("user");

            if (user == null) {
                return new ResponseEntity<>("User not found.", HttpStatus.UNAUTHORIZED);
            }

            boolean bookIsAvailable = bookService.checkIfBookIsAvailable(book.getId());

            if (!bookIsAvailable) {
                return new ResponseEntity<>("book is not available.", HttpStatus.BAD_REQUEST);
            }

            bookService.borrowBook(book.getId());

            BookActivityHistory bookActivityHistory = bookService.newActivity(book, user, 1);

            BookWithdrawal withdrawal = bookService.newWithdrawal(bookActivityHistory, user);

            return new ResponseEntity<>(withdrawal, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
