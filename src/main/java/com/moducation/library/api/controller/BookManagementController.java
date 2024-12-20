package com.moducation.library.api.controller;

import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookReturn;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.service.BookService;
import com.moducation.library.api.service.UserService;

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

import static com.moducation.library.api.utils.Constants.WITHDRAWAL_CODE;
import static com.moducation.library.api.utils.Constants.RETURN_CODE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestController
@RequestMapping("/books")
public class BookManagementController {
    private final BookService bookService;
    private final UserService userService;
    @Autowired
    public BookManagementController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/get-books")
    public ResponseEntity<Object> getBooks() {
        try {
            return new ResponseEntity<>(bookService.findAll(), OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(bookService.findById(id), OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addBook(@RequestBody Book book) {
        try {
            return new ResponseEntity<>(bookService.save(book), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Object> updateBook(@RequestBody Book book) {
        try {
            Book resultBook = bookService.findById(book.getId());

            if (resultBook == null) {
                return new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(bookService.updateBook(book), OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
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

            return new ResponseEntity<>(book, OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/borrow")
    public ResponseEntity<Object> borrowBook(@RequestBody Book book, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null) {
                log.error("user id is null: " + userId);
                return new ResponseEntity<>("please login", UNAUTHORIZED);
            }
            
            LibraryUser user = userService.getUserById(userId);

            if (user == null) {
                return new ResponseEntity<>("User not found.", UNAUTHORIZED);
            }

            boolean bookIsAvailable = bookService.checkIfBookIsAvailable(book.getId());

            if (!bookIsAvailable) {
                return new ResponseEntity<>("book is not available.", BAD_REQUEST);
            }

            bookService.borrowBook(book.getId());

            BookActivityHistory bookActivityHistory = bookService.newActivity(book, user, 1);

            BookWithdrawal withdrawal = bookService.newWithdrawal(bookActivityHistory, user);

            return new ResponseEntity<>(withdrawal, OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Object> returnBook(@RequestBody Book book, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null) {
                return new ResponseEntity<>("user id is null.", UNAUTHORIZED);
            }

            LibraryUser user = userService.getUserById(userId);

            if (user == null) {
                return new ResponseEntity<>("User not found.", UNAUTHORIZED);
            }

            bookService.returnBook(book.getId());

            BookActivityHistory bookActivityHistory = bookService.newActivity(book, user, RETURN_CODE);

            BookActivityHistory bookActivityHistoryForWithdrawal = bookService.getBookActivityHistory(WITHDRAWAL_CODE, user, book);

            BookWithdrawal bookWithdrawal = bookService.getBookWithdrawal(bookActivityHistoryForWithdrawal, user);

            BookReturn bookReturn = bookService.newReturn(bookActivityHistory, user, book, bookWithdrawal);

            return new ResponseEntity<>(bookReturn, OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }
}
