package com.moducation.library.api.service;

import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LibraryUserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private LibraryUser libraryUser;

    @BeforeEach
    public void setUp() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository);
        libraryUser = new LibraryUser();
        libraryUser.setId(1L);
        libraryUser.setUsername("testUser");
        libraryUser.setEmail("test@test.com");
        libraryUser.setPassword(encoder.encode("password123"));
    }

    @Test
    public void testVerifyPassword() {
        String password = "password123";
        boolean isValid = userService.verifyPassword(password, libraryUser.getPassword());
        assertTrue(isValid);
    }

    @Test
    public void testFindByUsername() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(libraryUser);

        LibraryUser result = userService.getUserByEmail("test@test.com");
        assertNotNull(result);
        assertEquals(libraryUser.getId(), result.getId());
        assertEquals(libraryUser.getEmail(), result.getEmail());
    }
    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(this.libraryUser));

        LibraryUser libraryUser = userService.getUserById(1L);

        assertEquals(1L, libraryUser.getId());
        assertEquals("testUser", libraryUser.getUsername());
    }

    @Test
    public void testGetUserByIdFail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        LibraryUser libraryUser = userService.getUserById(1L);

        assertNull(libraryUser);
    }

    @Test
    public void testGetUserByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(this.libraryUser);

        LibraryUser libraryUser = userService.getUserByUsername("testUser");

        assertEquals(1L, libraryUser.getId());
        assertEquals("testUser", libraryUser.getUsername());
    }

    @Test
    public void testSaveUser() {
        userService.saveUser(libraryUser);
        assertEquals(1L, libraryUser.getId());
        assertEquals("testUser", libraryUser.getUsername());
    }
}
