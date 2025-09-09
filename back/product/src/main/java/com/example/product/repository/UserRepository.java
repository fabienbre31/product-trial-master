package com.example.product.repository;

import com.example.product.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Get a user by using his emaail
     * @param email
     * @return
     */
    Optional<User> findByEmail(String email);
}
