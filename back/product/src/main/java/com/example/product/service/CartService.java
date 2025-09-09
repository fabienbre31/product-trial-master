package com.example.product.service;

import com.example.product.model.Cart;
import com.example.product.model.CartItem;
import com.example.product.model.Product;
import com.example.product.model.User;
import com.example.product.repository.CartRepository;
import com.example.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Get the cart of the user ; create an empty cart if it doesn't exist
     * @param user
     * @return
     */
    public Cart getCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            user.setCart(cart);
            cartRepository.save(cart); // persiste le panier vide
        }
        return cart;
    }

    /**
     * add a cartItem (product and his quantity) to the cart of a user ; if the product is already in cart, increment the quantity
     * @param user
     * @param productId
     * @param quantity
     * @return
     */
    public Cart addItem(User user, Long productId, int quantity) {
        Cart cart = getCart(user);

        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.getCartItems().add(item);
        }

        return cartRepository.save(cart);
    }

    /**
     * Remove the cartItem having the productId from the cart of the user ; if their is multiple, it remove all the stack
     * @param user
     * @param productId
     * @return
     */
    public Cart removeItem(User user, Long productId) {
        Cart cart = getCart(user);
        if (cart.getCartItems() != null) {
            cart.getCartItems().removeIf(i -> i.getProduct().getId().equals(productId));
        }
        return cartRepository.save(cart);
    }

    /**
     * Remove everything from the cart of the user
     * @param user
     */
    public void clearCart(User user) {
        Cart cart = getCart(user);
        if (cart.getCartItems() != null) {
            cart.getCartItems().clear();
        }
        cartRepository.save(cart);
    }
}
