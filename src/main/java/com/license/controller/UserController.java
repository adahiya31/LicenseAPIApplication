package com.license.controller;

import com.license.DTOs.CreateUserRequest;
import com.license.entityModel.User;
import com.license.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRoleNames()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
