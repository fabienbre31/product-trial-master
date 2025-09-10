package com.example.product.repository;

import com.example.product.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Get a user by using his email
     * @param email user's email
     * @return Optional<User> if found by email, Optional.empty() otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Get a user by using his username
     * @param username user's username
     * @return Optional<User> if found by username, Optional.empty() otherwise
     */
    Optional<User> findByUsername(String username);
}
