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
     * @param username
     * @param firstName
     * @param password
     * @param email
     * @return
     */
    public User createUser(String username, String firstName, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstName);
        user.setPassword(password);
        user.setEmail(email);
        return repository.save(user);
    }

    /**
     * authenticate a user by mail and password and return the user or empty
     * @param email
     * @param password
     * @return
     */
    public Optional<User> authenticate(String email, String password) {
        return repository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }

    /**
     * find a user by mail and return it or empty
     * @param email
     * @return
     */
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

}
