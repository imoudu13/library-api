package com.moducation.library.api.repositories;

import com.moducation.library.api.models.BookActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookActivityHistoryRepository extends JpaRepository<BookActivityHistory, Long> {}
