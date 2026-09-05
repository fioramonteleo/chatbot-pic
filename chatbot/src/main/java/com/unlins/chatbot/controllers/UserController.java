package com.unlins.chatbot.controllers;

import com.unlins.chatbot.dtos.LoginRequestDTO;
import com.unlins.chatbot.entities.User;
import com.unlins.chatbot.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user){

        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);


    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDTO loginData) {
        try {
            User authenticatedUser = userService.authenticate(loginData.login(), loginData.password());
            return ResponseEntity.ok(authenticatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // Retorna erro 400 se falhar
        }
    }
}
