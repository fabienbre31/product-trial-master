package com.example.product.controller;

import com.example.product.model.Product;
import com.example.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // --- Lecture : accessible à tous les utilisateurs authentifiés ---
    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Ajout : seulement admin ---
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (!"admin@admin.com".equals(email)) {
            //System.out.println("mauvais email sur ajout => l'email n'est pas admin@admin.com mais "+email);
            return ResponseEntity.status(403).build(); // Forbidden
        }
        //System.out.println("email OK sur ajout => "+email);
        return ResponseEntity.ok(service.save(product));
    }

    // --- Modification : seulement admin ---
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (!"admin@admin.com".equals(email)) {
            //System.out.println("mauvais email sur modification => l'email n'est pas admin@admin.com mais "+email);
            return ResponseEntity.status(403).build();
        }
        //System.out.println("email OK sur modification => "+email);
        return service.getById(id).map(existing -> {
            product.setId(id);
            return ResponseEntity.ok(service.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- Suppression : seulement admin ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (!"admin@admin.com".equals(email)) {
            return ResponseEntity.status(403).build();
        }
        if (!service.getById(id).isPresent())
            return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
