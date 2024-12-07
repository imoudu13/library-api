package com.moducation.library.api.controller;

import com.moducation.library.api.models.User;
import com.moducation.library.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/users")
public class UserManagementController {
    private final UserService userService;

    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody User user) {
        try {
            User userFromDB = this.userService.getUserByUsername(user.getUsername());

            if (userFromDB == null) {
                return new ResponseEntity<>("Incorrect Username.", HttpStatus.NOT_FOUND);
            }

            boolean validPassword = this.userService.verifyPassword(user.getPassword(), userFromDB.getPassword());

            if (validPassword) {
                return new ResponseEntity<>(userFromDB, HttpStatus.OK);
            }

            return new ResponseEntity<>("Incorrect password.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        try {
            User userByUsername = userService.getUserByUsername(user.getUsername());

            if (userByUsername != null) {
                return new ResponseEntity<>("User with this username already exists.", HttpStatus.CONFLICT);
            }

            User userByEmail = userService.getUserByEmail(user.getEmail());

            if (userByEmail != null) {
                return new ResponseEntity<>("User with this email already exists.", HttpStatus.CONFLICT);
            }

            userService.saveUser(user);

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);

            if (user == null) {
                return new ResponseEntity<>("There is no user with that id in the system.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
