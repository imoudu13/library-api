package com.moducation.library.api.service;

import com.moducation.library.api.models.User;
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
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository);
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@test.com");
        user.setPassword(encoder.encode("password123"));
    }

    @Test
    public void testVerifyPassword() {
        String password = "password123";
        boolean isValid = userService.verifyPassword(password, user.getPassword());
        assertTrue(isValid);
    }

    @Test
    public void testFindByUsername() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);

        User result = userService.getUserByEmail("test@test.com");
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }
    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        User user = userService.getUserById(1L);

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testGetUserByIdFail() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User user = userService.getUserById(1L);

        assertNull(user);
    }

    @Test
    public void testGetUserByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        User user = userService.getUserByUsername("testUser");

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testSaveUser() {
        userService.saveUser(user);
        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
    }
}
