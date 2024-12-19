package com.moducation.library.api.repositories;

import com.moducation.library.api.models.Book;
import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookActivityHistoryRepository extends JpaRepository<BookActivityHistory, Long> {
    @Query("SELECT b FROM BookActivityHistory b WHERE b.libraryUser = :libraryUser AND b.book = :book AND b.type = :type ORDER BY b.date DESC LIMIT 1")
    BookActivityHistory findBookActivityForWithdrawal(@Param("type") Integer type,
                                                      @Param("libraryUser") LibraryUser libraryUser,
                                                      @Param("book") Book book);
}
