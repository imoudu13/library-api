package com.moducation.library.api.service;

import com.moducation.library.api.exceptions.IncorrectFilterException;
import com.moducation.library.api.models.Book;
import com.moducation.library.api.repositories.BookRepository;

import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
}
