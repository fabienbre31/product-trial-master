package com.example.product;

import com.example.product.model.Product;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;
    private Product testProduct;

    @BeforeEach
    void setup() {
        // Générer JWT pour admin
        adminToken = Jwts.builder()
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(com.example.product.config.SecurityConfig.JWT_SECRET)
                .compact();

        // Générer JWT pour un utilisateur normal
        userToken = Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(com.example.product.config.SecurityConfig.JWT_SECRET)
                .compact();

        // Créer un produit de test
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
    }

    @Test
    void getProducts_requiresAuthentication() throws Exception {
        // Sans token -> 401
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());

        // Avec token utilisateur normal -> 200
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanCreateUpdateDeleteProduct() throws Exception {
        // POST produit -> 200
        MvcResult postResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andReturn();

        Product created = objectMapper.readValue(postResult.getResponse().getContentAsString(), Product.class);
        Long productId = created.getId();

        // PUT produit -> 200
        created.setName("Updated Name");
        mockMvc.perform(put("/api/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());

        // DELETE produit -> 204
        mockMvc.perform(delete("/api/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void nonAdminCannotCreateUpdateDeleteProduct() throws Exception {
        // POST -> 403
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isForbidden());

        // PUT -> 403
        mockMvc.perform(put("/api/products/1")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isForbidden());

        // DELETE -> 403
        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProductById() throws Exception {
        // Créer produit avec admin
        MvcResult postResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andReturn();

        Product created = objectMapper.readValue(postResult.getResponse().getContentAsString(), Product.class);
        Long productId = created.getId();

        // GET par ID -> 200
        mockMvc.perform(get("/api/products/" + productId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // GET produit inexistant -> 404
        mockMvc.perform(get("/api/products/9999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
