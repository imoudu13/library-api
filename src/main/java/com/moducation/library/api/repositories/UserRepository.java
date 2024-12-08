package com.moducation.library.api.repositories;

import com.moducation.library.api.models.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<LibraryUser, Long> {
    LibraryUser findByUsername(String username);
    LibraryUser findByEmail(String email);
}
