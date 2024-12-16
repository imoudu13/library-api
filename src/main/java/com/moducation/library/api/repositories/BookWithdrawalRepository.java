package com.moducation.library.api.repositories;

import com.moducation.library.api.models.BookWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookWithdrawalRepository extends JpaRepository<BookWithdrawal, Long> {
}
