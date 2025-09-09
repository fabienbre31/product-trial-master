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

    /**
     * get the cart owner using the email attribute of the request
     * @param request
     * @return
     */
    private User getCurrentUser(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Service to get the cart associated to the request email attribute
     * @param request
     * @return
     */
    @GetMapping
    public Cart getCart(HttpServletRequest request) {
        return cartService.getCart(getCurrentUser(request));
    }

    /**
     * Service to add a "cartItem" to the cart associated to the email attribute of the request ;
     * A cartItem is a product with a quantity
     * @param request
     * @param productId
     * @param quantity
     * @return
     */
    @PostMapping("/add")
    public Cart addItem(HttpServletRequest request,
                        @RequestParam Long productId,
                        @RequestParam int quantity) {
        return cartService.addItem(getCurrentUser(request), productId, quantity);
    }

    /**
     * Service to remove a product from the cart associated to the email attribute of the request
     * @param request
     * @param productId
     * @return
     */
    @PostMapping("/remove")
    public Cart removeItem(HttpServletRequest request,
                           @RequestParam Long productId) {
        return cartService.removeItem(getCurrentUser(request), productId);
    }

    /**
     * Service to clear the cart associated to the email attribute of the request
     * @param request
     * @return
     */
    @PostMapping("/clear")
    public Cart clearCart(HttpServletRequest request) {
        cartService.clearCart(getCurrentUser(request));
        return cartService.getCart(getCurrentUser(request));
    }
}
