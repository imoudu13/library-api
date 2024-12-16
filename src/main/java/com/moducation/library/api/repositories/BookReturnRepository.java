package com.moducation.library.api.repositories;

import com.moducation.library.api.models.BookReturn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReturnRepository extends JpaRepository<BookReturn, Long> {
}
