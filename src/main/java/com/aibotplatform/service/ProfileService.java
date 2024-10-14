package com.aibotplatform.service;

import com.aibotplatform.model.User;
import com.aibotplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    @Transactional
    public void changeUsername(String currentUsername, String newUsername) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }
}
