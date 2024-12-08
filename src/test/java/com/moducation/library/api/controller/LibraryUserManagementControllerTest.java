package com.moducation.library.api.controller;

import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class LibraryUserManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserManagementController userController;

    String username = "username";
    String password = "password";
    String email = "email";

    PasswordEncoder passwordEncoder;
    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    public void testLoginUserSuccess() throws Exception {
        LibraryUser libraryUser = LibraryUser.builder().id(1L).username(username).password(passwordEncoder.encode(password)).build();
        when(userService.getUserByUsername(username)).thenReturn(libraryUser);
        when(userService.verifyPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginUserInvalidPassword() throws Exception {
        LibraryUser libraryUser = LibraryUser.builder().id(1L).username(username).password(passwordEncoder.encode(password)).build();
        when(userService.getUserByUsername(username)).thenReturn(libraryUser);
        when(userService.verifyPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginUserIncorrectUsername() throws Exception {
        when(userService.getUserByUsername(username)).thenReturn(null);
        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testLoginUserException() throws Exception {
        when(userService.getUserByUsername(username)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        LibraryUser libraryUser = LibraryUser.builder().id(1L).username("testuser").build();
        when(userService.saveUser(Mockito.any(LibraryUser.class))).thenReturn(libraryUser);

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{ \"firstname\": \"testuser\", \"username\": \"testusername\" }"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstname", is("testuser")))
                .andExpect(jsonPath("$.username", is("testusername")));
    }

    @Test
    public void testRegisterUserInvalidUsername() throws Exception {
        LibraryUser libraryUser = LibraryUser.builder().id(1L).username(username).build();
        when(userService.getUserByUsername(username)).thenReturn(libraryUser);

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{ \"firstname\": \"testuser\", \"username\": \"username\" }"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testRegisterUserInvalidEmail() throws Exception {
        LibraryUser libraryUser = LibraryUser.builder().id(1L).username(username).email(email).build();
        when(userService.getUserByEmail(email)).thenReturn(libraryUser);

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{ \"firstname\": \"testuser\", \"username\": \"username\", \"email\": \"email\" }"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testRegisterUserFailure() throws Exception {
        when(userService.saveUser(Mockito.any(LibraryUser.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/users/register")
                        .contentType("application/json")
                        .content("{ \"firstname\": \"testuser\", \"username\": \"testusername\" }"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetUserFound() throws Exception {
        LibraryUser libraryUser = LibraryUser.builder().id(1L).username("testusername").firstname("testuser").build();
        when(userService.getUserById(1L)).thenReturn(libraryUser);

        mockMvc.perform(get("/users/1/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.firstname", is("testuser")))
                .andExpect(jsonPath("$.username", is("testusername")));
    }

    @Test
    public void testGetUserNotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(null);

        mockMvc.perform(get("/users/1/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserFailure() throws Exception {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/users/1/profile"))
                .andExpect(status().isInternalServerError());
    }
}