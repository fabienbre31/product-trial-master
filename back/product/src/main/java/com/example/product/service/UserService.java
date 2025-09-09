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

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public Optional<User> authenticate(String email, String password) {
        return repository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }
}
