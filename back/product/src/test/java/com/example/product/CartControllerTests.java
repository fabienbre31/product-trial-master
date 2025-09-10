package com.example.product;

import com.example.product.model.Cart;
import com.example.product.model.Product;
import com.example.product.repository.UserRepository;
import com.example.product.security.JwtFilter;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTests {

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
        // Créer un utilisateur pour les tests s'il n'existe pas deja
        if (userRepository.findByEmail("user@example.com").isEmpty()){
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

        // Créer un produit pour les tests
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

        // Générer token utilisateur
        userToken = generateUserToken();
    }

    private String generateUserToken() {
        return Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(JwtFilter.JWT_SECRET)
                .compact();
    }

    private String generateAdminToken() {
        return Jwts.builder()
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(JwtFilter.JWT_SECRET)
                .compact();
    }

    @Test
    void getCart_requiresAuthentication() throws Exception {
        // Sans token -> 401
        mockMvc.perform(get("/api/cart")).andExpect(status().isUnauthorized());

        // Avec token utilisateur -> 200
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void addItemToCart() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andReturn();

        Cart cart = objectMapper.readValue(result.getResponse().getContentAsString(), Cart.class);
        assertThat(cart.getCartItems()).hasSize(1);
        assertThat(cart.getCartItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void removeItemFromCart() throws Exception {
        // Ajouter un produit
        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString())
                        .param("quantity", "1"))
                .andExpect(status().isOk());

        // Supprimer le produit
        MvcResult result = mockMvc.perform(post("/api/cart/remove")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andReturn();

        Cart cart = objectMapper.readValue(result.getResponse().getContentAsString(), Cart.class);
        assertThat(cart.getCartItems()).isEmpty();
    }

    @Test
    void clearCart() throws Exception {
        // Ajouter plusieurs produits
        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + userToken)
                        .param("productId", productId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isOk());

        // Vider le panier
        MvcResult result = mockMvc.perform(post("/api/cart/clear")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        Cart cart = objectMapper.readValue(result.getResponse().getContentAsString(), Cart.class);
        assertThat(cart.getCartItems()).isEmpty();
    }
}
