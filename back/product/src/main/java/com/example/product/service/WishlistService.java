package com.example.product.service;

import com.example.product.model.*;
import com.example.product.repository.*;
import org.springframework.stereotype.Service;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepo, ProductRepository productRepo) {
        this.wishlistRepository = wishlistRepo;
        this.productRepository = productRepo;
    }

    /**
     * get the user wishlist and return it
     * @param user
     * @return
     */
    public Wishlist getWishlist(User user) {
        Wishlist wishlist = user.getWishlist();
        if (wishlist == null) {
            wishlist = new Wishlist();
            user.setWishlist(wishlist);
            wishlistRepository.save(wishlist); // on persiste le panier vide
        }
        return wishlist;
    }

    /**
     * add a product by its ID to the wishlist of a user and return the wishlist
     * @param user
     * @param productId
     * @return
     */
    public Wishlist addItem(User user, Long productId) {
        Wishlist wishlist = getWishlist(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!(wishlist.getProducts().contains(product))) {
            wishlist.getProducts().add(product);
        }

        return wishlistRepository.save(wishlist);
    }

    /**
     * remove a product by its ID from the wishlist of a user and return the wishlist
     * @param user
     * @param productId
     * @return
     */
    public Wishlist removeItem(User user, Long productId) {
        Wishlist wishlist = getWishlist(user);
        wishlist.getProducts().removeIf(i -> i.getId().equals(productId));
        return wishlistRepository.save(wishlist);
    }

    /**
     * clear the wishlist of a user
     * @param user
     */
    public void clearWishlist(User user) {
        Wishlist wishlist = getWishlist(user);
        wishlist.getProducts().clear();
        wishlistRepository.save(wishlist);
    }
}
