package com.moducation.library.api.repositories;

import com.moducation.library.api.models.BookWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookWithdrawalRepository extends JpaRepository<BookWithdrawal, Long> {
}
