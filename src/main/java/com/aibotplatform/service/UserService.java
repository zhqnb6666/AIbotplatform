package com.aibotplatform.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());
    }

    public User registerNewUser(User user) {
        // Hash the password before saving
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Set default values for credits, createdAt, and updatedAt
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);  // Set current time as createdAt
        user.setUpdatedAt(currentTimestamp);  // Set current time as updatedAt

        // Set the default role if not already set (it defaults to USER)
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        return userRepository.save(user);
    }
}