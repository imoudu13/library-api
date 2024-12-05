package com.moducation.library.api.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private User user;

    @BeforeEach
    void setup() {
        user = new User(1L, "username", "password", "firstname", "lastname", (byte) 1);
    }

    @Test
    void testAllAttributes() {
        assertEquals("firstname", user.getFirstname());
        assertEquals("lastname", user.getLastname());
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(1L, user.getId());
        assertEquals((byte) 1, user.getRole());
    }

}
