package com.aibotplatform.controller;

import com.aibotplatform.model.User;
import com.aibotplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @GetMapping("/forgot-password")
    public String showForgetPasswordForm(Model model){
        model.addAttribute("user",new User());
        return "forgot-password";
    }

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

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String verificationCode,
                                           @RequestParam String newPassword) {
        try {
            userService.resetPassword(email, verificationCode, newPassword);
            return ResponseEntity.ok().body("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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