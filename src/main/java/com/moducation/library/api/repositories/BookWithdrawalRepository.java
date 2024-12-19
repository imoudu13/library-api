package com.moducation.library.api.repositories;

import com.moducation.library.api.models.BookActivityHistory;
import com.moducation.library.api.models.BookWithdrawal;
import com.moducation.library.api.models.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookWithdrawalRepository extends JpaRepository<BookWithdrawal, Long> {

    @Query("SELECT bw FROM BookWithdrawal bw WHERE bw.bookActivity = :bookActivityHistory AND bw.libraryUser = :libraryUser")
    BookWithdrawal findByBookActivityHistoryAndLibraryUser(@Param("bookActivityHistory") BookActivityHistory bookActivityHistory, @Param("libraryUser") LibraryUser libraryUser);
}
