package com.moducation.library.api.service;

import com.moducation.library.api.models.User;
import com.moducation.library.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        Optional<User> optionalUser = userService.getUserById(1L);
        User retrievedUser = optionalUser.get();

        assertEquals(1L, retrievedUser.getId());
        assertEquals("testUser", retrievedUser.getUsername());
        assertEquals("password123", retrievedUser.getPassword());
    }

    @Test
    public void testGetUserByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        User user = userService.getUserByUsername("testUser");

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testSaveUser() {
        userService.saveUser(user);
        assertNotEquals("password123", user.getPassword());
    }
}
