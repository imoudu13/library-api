package com.moducation.library.api.controller;

import com.moducation.library.api.models.User;
import com.moducation.library.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(userService.saveUser(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{ \"firstname\": \"testuser\", \"username\": \"testusername\" }"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstname", is("testuser")))
                .andExpect(jsonPath("$.username", is("testusername")));
    }

    @Test
    void testRegisterUserFailure() throws Exception {
        when(userService.saveUser(Mockito.any(User.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{ \"firstname\": \"testuser\", \"username\": \"testusername\" }"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetUserFound() throws Exception {
        User user = User.builder().id(1L).username("testusername").firstname("testuser").build();
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstname", is("testuser")))
                .andExpect(jsonPath("$.username", is("testusername")));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(null);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserFailure() throws Exception {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isInternalServerError());
    }
}