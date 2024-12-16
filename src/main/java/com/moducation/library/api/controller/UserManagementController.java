package com.moducation.library.api.controller;

import com.moducation.library.api.models.LibraryUser;
import com.moducation.library.api.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.SessionAttribute;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserManagementController {
    private final UserService userService;

    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody LibraryUser libraryUser, HttpSession session) {
        try {
            LibraryUser libraryUserFromDB = this.userService.getUserByUsername(libraryUser.getUsername());

            if (libraryUserFromDB == null) {
                return new ResponseEntity<>("Incorrect Username.", HttpStatus.NOT_FOUND);
            }

            boolean validPassword = this.userService.verifyPassword(libraryUser.getPassword(), libraryUserFromDB.getPassword());

            if (validPassword) {
                session.setAttribute("userId", libraryUserFromDB.getId());
                return new ResponseEntity<>(libraryUserFromDB, HttpStatus.OK);
            }

            return new ResponseEntity<>("Incorrect password.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody LibraryUser libraryUser) {
        try {
            LibraryUser libraryUserByUsername = userService.getUserByUsername(libraryUser.getUsername());

            if (libraryUserByUsername != null) {
                return new ResponseEntity<>("User with this username already exists.", HttpStatus.CONFLICT);
            }

            LibraryUser libraryUserByEmail = userService.getUserByEmail(libraryUser.getEmail());

            if (libraryUserByEmail != null) {
                return new ResponseEntity<>("User with this email already exists.", HttpStatus.CONFLICT);
            }

            userService.saveUser(libraryUser);

            return new ResponseEntity<>(libraryUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) {
        try {
            LibraryUser libraryUser = userService.getUserById(id);

            if (libraryUser == null) {
                return new ResponseEntity<>("There is no user with that id in the system.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(libraryUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check-user")
    public String checkUser(@SessionAttribute(value = "userId", required = false) String userId) {
        if (userId != null) {
            return "UserId in session: " + userId;
        } else {
            return "No UserId found in session!";
        }
    }
}
