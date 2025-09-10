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
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * create account with payload {username, firstName, password, email}
     * @param payload
     * @return
     */
    @PostMapping("/account")
    public ResponseEntity<User> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String firstName = payload.get("firstName");
        String password = payload.get("password");
        String email = payload.get("email");
        return ResponseEntity.ok(userService.createUser(username, firstName, password, email));
    }

    /**
     * create authentification token with payload {email,password}
     * @param payload
     * @return
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        return userService.authenticate(email, password)
                .map(user -> {
                    String token = Jwts.builder()
                            .setSubject(user.getEmail())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 jour
                            .signWith(JwtFilter.JWT_SECRET)
                            .compact();
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
