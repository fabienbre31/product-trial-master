package com.example.product;

import com.example.product.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldAddProductAndIncreaseProductCount() throws Exception {
        //Récupérer le nombre de produits avant l'ajout
        MvcResult beforeResult = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn();

        Product[] beforeProducts = objectMapper.readValue(
                beforeResult.getResponse().getContentAsString(),
                Product[].class
        );
        int initialCount = beforeProducts.length;

        //Créer un nouveau produit
        Product newProduct = new Product();
        newProduct.setCode("TEST-123");
        newProduct.setName("Test Product");
        newProduct.setDescription("Test Description");
        newProduct.setCategory("Test Category");
        newProduct.setPrice(9.99);
        newProduct.setQuantity(10);
        newProduct.setInternalReference("REF-TEST");
        newProduct.setShellId(999L);
        newProduct.setInventoryStatus(Product.InventoryStatus.INSTOCK);
        newProduct.setRating(5);

        //Envoyer un POST pour ajouter le produit
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        // 4. Vérifier que le nombre de produits a augmenté de 1
        MvcResult afterResult = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn();

        Product[] afterProducts = objectMapper.readValue(
                afterResult.getResponse().getContentAsString(),
                Product[].class
        );

        assertThat(afterProducts.length).isEqualTo(initialCount + 1);
    }
}
