package com.example.product.controller;

import com.example.product.model.User;
import com.example.product.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final String jwtSecret = "ThisIsAReallyLongSecretKeyForTestsThatIsSecureEnough123!";

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/account")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

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
                            .signWith(Keys.hmacShaKeyFor(com.example.product.config.SecurityConfig.JWT_SECRET.getBytes()))
                            .compact();
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
