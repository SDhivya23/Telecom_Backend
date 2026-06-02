package com.example.controllers;

import com.example.entities.User;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{id}")
    public User getProfileById(@PathVariable Integer id,
                               Authentication auth) {

        return userService.getProfileById(id, auth.getName());
    }


    @PutMapping("/profile")
    public User updateProfile(@RequestBody User updatedUser,
                              Authentication authentication) {

        String email = authentication.getName();
        return userService.updateProfile(email, updatedUser);
    }
}