package com.moducation.library.api.repositories;

import com.moducation.library.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);
    public User findByUserId(Long userId);
}
