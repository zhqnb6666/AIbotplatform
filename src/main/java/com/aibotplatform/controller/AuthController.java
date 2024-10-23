package com.aibotplatform.controller;

import com.aibotplatform.dto.authDTO.AuthResponse;
import com.aibotplatform.dto.authDTO.LoginRequest;
import com.aibotplatform.dto.authDTO.PasswordResetRequest;
import com.aibotplatform.dto.authDTO.RegisterRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.User;
import com.aibotplatform.service.impl.UserServiceImpl;
import com.aibotplatform.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "APIs for login, register, password reset and email verification")
public class AuthController {

    private final UserServiceImpl userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public AuthController(UserServiceImpl userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Login user with username or email and password")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect username or password");
        }

        final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsernameOrEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register new user with username, email, password and verification code")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPasswordHash(registerRequest.getPassword());
            userService.registerNewUser(user, registerRequest.getVerificationCode());
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-verification/{email}")
    @Operation(summary = "Send verification code", description = "Send verification code to email")
    public ResponseEntity<?> sendVerificationCode(@PathVariable String email) {
        try {
            userService.sendVerificationCode(email);
            return ResponseEntity.ok().body("Verification code sent successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password with email, verification code and new password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getVerificationCode(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}