package com.example.product.controller;

import com.example.product.model.User;
import com.example.product.security.JwtFilter;
import com.example.product.service.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * create account with payload {username, firstname, password, email}
     * @param payload {username, firstname, password, email}
     * @return
     */
    @PostMapping("/account")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String firstname = payload.get("firstname");
        String password = payload.get("password");
        String email = payload.get("email");

        // Fields are mandatory
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }
        if (firstname == null || firstname.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Firstname is required"));
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
        }
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        // Username is unique
        if (userService.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Username already exists"));
        }

        // Email is unique
        if (userService.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already exists"));
        }

        User user = userService.createUser(username, firstname, password, email);
        return ResponseEntity.ok(user);
    }

    /**
     * create authentication token with payload {email, password}
     * @param payload
     * @return JWT token if authentication succeeds, error message otherwise
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        // Fields are mandatory
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
        }

        return userService.findByEmail(email)
                .map(user -> {
                    if (!userService.authenticate(email, password).isPresent()) {
                        return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
                    }
                    // Génération du token JWT
                    String token = Jwts.builder()
                            .setSubject(user.getEmail())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 jour
                            .signWith(JwtFilter.JWT_SECRET)
                            .compact();
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

}
