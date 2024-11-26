package com.aibotplatform.repository;

import com.aibotplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByUsernameOrEmail(String username, String email);

    User findByUserId(long userId);

    long count();

    long countByRole(User.Role role);

    List<User>  findUsersByUsernameIsContainingIgnoreCase(String key);
}