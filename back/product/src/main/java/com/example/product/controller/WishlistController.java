package com.example.product.controller;

import com.example.product.model.User;
import com.example.product.model.Wishlist;
import com.example.product.service.UserService;
import com.example.product.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    /**
     * get the wishlist owner  using the email attribute of the request
     * @param request
     * @return
     */
    private User getCurrentUser(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        return userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Service to get the wishlist associated to the requester
     * @param request
     * @return
     */
    @GetMapping
    public Wishlist getWishlist(HttpServletRequest request) {
        return wishlistService.getWishlist(getCurrentUser(request));
    }

    /**
     * Service to add a product to the wishlist associated to the email attribute of the request
     * @param request
     * @param productId
     * @return
     */
    @PostMapping("/add")
    public Wishlist addItem(HttpServletRequest request,
                        @RequestParam Long productId) {
        return wishlistService.addItem(getCurrentUser(request), productId);
    }

    /**
     * Service to remove a product from the wishlist associated to the email attribute of the request
     * @param request
     * @param productId
     * @return
     */
    @PostMapping("/remove")
    public Wishlist removeItem(HttpServletRequest request,
                           @RequestParam Long productId) {
        return wishlistService.removeItem(getCurrentUser(request), productId);
    }

    /**
     * Service to clear a wishlist associated to the email attribute of the request
     * @param request
     * @return
     */
    @PostMapping("/clear")
    public Wishlist clearWishlist(HttpServletRequest request) {
        wishlistService.clearWishlist(getCurrentUser(request));
        return wishlistService.getWishlist(getCurrentUser(request));
    }
}
