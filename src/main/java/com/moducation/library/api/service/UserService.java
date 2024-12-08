package com.moducation.library.api.service;

import com.moducation.library.api.models.LibraryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moducation.library.api.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public LibraryUser getUserById(Long id) {
        Optional<LibraryUser> user = userRepository.findById(id);

        return user.orElse(null);
    }

    public LibraryUser getUserByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public LibraryUser getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public LibraryUser saveUser(LibraryUser libraryUser) {
        String encodedPassword = encoder.encode(libraryUser.getPassword());
        libraryUser.setPassword(encodedPassword);
        return userRepository.save(libraryUser);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return this.encoder.matches(rawPassword, encodedPassword);
    }
}
