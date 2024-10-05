package com.aibotplatform.controller;

import com.aibotplatform.model.User;
import com.aibotplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerNewUser(user);
        return "redirect:/login";
    }

    @GetMapping("/hello")
    public String hello(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);
        return "hello";
    }
}