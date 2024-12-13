package com.moducation.library.api.repositories;

import com.moducation.library.api.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT id, title, author, genre, availability, avgRating, sumRating, numberOfRatings FROM Book WHERE genre LIKE %:genre%")
    List<Book> findByGenre(@Param("genre") String genre);

    @Query("SELECT id, title, author, genre, availability, avgRating, sumRating, numberOfRatings FROM Book WHERE title LIKE %:title%")
    List<Book> findByTitle(@Param("title") String title);

    @Query("SELECT id, title, author, genre, availability, avgRating, sumRating, numberOfRatings FROM Book WHERE author LIKE %:author%")
    List<Book> findByAuthor(@Param("author") String author);

    @Modifying
    @Query("UPDATE Book SET title = :title, author = :author, genre = :genre, availability = :availability WHERE id = :id")
    Book updateBook(@Param("title") String title, @Param("author") String author, @Param("genre") String genre, @Param("availability") Integer availability, @Param("id") Long id);

    @Query("SELECT availability FROM Book WHERE id = :id")
    Integer getAvailability(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Book SET availability = :newAvailability WHERE id = :id")
    void updateAvailability(@Param("newAvailability") Integer newAvailability, @Param("id") Long id);
}
