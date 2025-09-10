package com.example.product.service;

import com.example.product.model.User;
import com.example.product.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * create a user by using a username, a firstName, a password and an email and return it
     * @param username the user's username
     * @param firstname the user's firstname
     * @param password the user's password
     * @param email the user's email
     * @return created User without exposing the password
     * @throws IllegalArgumentException if required fields are missing
     */
    public User createUser(String username, String firstname, String password, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (firstname == null || firstname.isBlank()) {
            throw new IllegalArgumentException("Firstname is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (repository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        return repository.save(user);
    }

    /**
     * Authenticate a user by email and password and return the user or empty
     * @param email the user's email
     * @param password the user's password
     * @return Optional<User> if authentication succeeds, Optional.empty() otherwise
     */
    public Optional<User> authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty(); // Empty fields mandatory
        }

        return repository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }


    /**
     * find a user by mail and return it or empty
     * @param email the user's email
     * @return Optional<User> if found by email, Optional.empty() otherwise
     */
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * find a user by username and return it or empty
     * @param username user's username
     * @return Optional<User> if found by username, Optional.empty() otherwise
     */
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
