package com.aibotplatform.controller;

import com.aibotplatform.model.User;
import com.aibotplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;



@Controller
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute("user") User user) {
//        userService.registerNewUser(user);
//        return "redirect:/login";
//    }

    @GetMapping("/hello")
    public String hello(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        return "hello";
    }

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        System.out.println("Sending verification code to " + email);
        userService.sendVerificationCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam String verificationCode) {
        try {
            userService.registerNewUser(user, verificationCode);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            return "register";
        }
    }
}