package com.example.product;

import com.example.product.model.User;
import com.example.product.model.Wishlist;
import com.example.product.model.Product;
import com.example.product.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WishlistControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private Product testProduct;
    private Long productId;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws Exception {
        // Create user if not existing
        Optional<User> user = userRepository.findByEmail("user@example.com");
        if (user.isEmpty()){
            String userJson = """
            {
                "username": "testuser",
                "firstname": "Test",
                "email": "user@example.com",
                "password": "password"
            }
            """;
            mockMvc.perform(post("/auth/account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk());
        }

        // Create product for tests
        testProduct = new Product();
        testProduct.setCode("TEST-123");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setCategory("Test Category");
        testProduct.setPrice(9.99);
        testProduct.setQuantity(10);
        testProduct.setInternalReference("REF-TEST");
        testProduct.setShellId(999L);
        testProduct.setInventoryStatus(Product.InventoryStatus.INSTOCK);
        testProduct.setRating(5);

        MvcResult postResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + generateAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andReturn();

        Product created = objectMapper.readValue(postResult.getResponse().getContentAsString(), Product.class);
        productId = created.getId();

        // Generate user token
        userToken = generateUserToken();
    }

    private String generateUserToken() {
        return Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(com.example.product.config.SecurityConfig.JWT_SECRET)
                .compact();
    }

    private String generateAdminToken() {
        return Jwts.builder()
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(com.example.product.config.SecurityConfig.JWT_SECRET)
                .compact();
    }

    @Test
    void getWishlistWithAndWithoutAuthentication() throws Exception {
        // Sans token -> 401
        mockMvc.perform(get("/api/wishlist")).andExpect(status().isUnauthorized());

        // Avec token utilisateur -> 200
        mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void addAndRemoveWishlistProduct() throws Exception {
        // Ajouter le produit
        MvcResult result = mockMvc.perform(post("/api/wishlist/add")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andReturn();

        Wishlist wishlist = objectMapper.readValue(result.getResponse().getContentAsString(), Wishlist.class);
        assertThat(wishlist.getProducts()).hasSize(1);

        // Supprimer le produit
        result = mockMvc.perform(post("/api/wishlist/remove")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andReturn();

        wishlist = objectMapper.readValue(result.getResponse().getContentAsString(), Wishlist.class);
        assertThat(wishlist.getProducts()).isEmpty();
    }


    @Test
    void clearWishlist() throws Exception {
        // Ajouter plusieurs produits
        mockMvc.perform(post("/api/wishlist/add")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk());

        // Vider le panier
        MvcResult result = mockMvc.perform(post("/api/wishlist/clear")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        Wishlist wishlist = objectMapper.readValue(result.getResponse().getContentAsString(), Wishlist.class);
        assertThat(wishlist.getProducts()).isEmpty();
    }
}
