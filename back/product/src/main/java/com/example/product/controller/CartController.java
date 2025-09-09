package com.example.product.controller;

import com.example.product.model.Cart;
import com.example.product.model.User;
import com.example.product.service.CartService;
import com.example.product.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private User getCurrentUser(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public Cart getCart(HttpServletRequest request) {
        return cartService.getCart(getCurrentUser(request));
    }

    @PostMapping("/add")
    public Cart addItem(HttpServletRequest request,
                        @RequestParam Long productId,
                        @RequestParam int quantity) {
        return cartService.addItem(getCurrentUser(request), productId, quantity);
    }

    @PostMapping("/remove")
    public Cart removeItem(HttpServletRequest request,
                           @RequestParam Long productId) {
        return cartService.removeItem(getCurrentUser(request), productId);
    }

    @PostMapping("/clear")
    public Cart clearCart(HttpServletRequest request) {
        cartService.clearCart(getCurrentUser(request));
        return cartService.getCart(getCurrentUser(request));
    }
}
