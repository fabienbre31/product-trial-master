package com.example.product.service;

import com.example.product.model.Cart;
import com.example.product.model.CartItem;
import com.example.product.model.Product;
import com.example.product.model.User;
import com.example.product.repository.CartItemRepository;
import com.example.product.repository.CartRepository;
import com.example.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final ProductRepository productRepo;

    public CartService(CartRepository cartRepo, CartItemRepository itemRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
    }

    public Cart getCart(User user) {
        return cartRepo.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepo.save(cart);
        });
    }

    public Cart addItem(User user, Long productId, int quantity) {
        Cart cart = getCart(user);
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }

        return cartRepo.save(cart);
    }

    public Cart removeItem(User user, Long productId) {
        Cart cart = getCart(user);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        return cartRepo.save(cart);
    }

    public void clearCart(User user) {
        Cart cart = getCart(user);
        cart.getItems().clear();
        cartRepo.save(cart);
    }
}
