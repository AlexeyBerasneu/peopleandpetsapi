package com.sorokin.peopleandpetsapi.controller;

import com.sorokin.peopleandpetsapi.model.User;
import com.sorokin.peopleandpetsapi.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid
                                           @RequestBody User user) {
        log.info("Create user: {}", user);
        User newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id")
                        @NotNull
                        @Positive Long id) {
        log.info("Get user: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Get all users");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id")
                                           @NotNull
                                           @Positive Long id) {
        log.info("Delete user: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id")
                                           @NotNull
                                           @Positive Long id,
                                           @RequestBody
                                           @Valid User user) {
        log.info("Update user: {}", user);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, user));
    }
}
