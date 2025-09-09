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

    /**
     * Service to get all the products
     */
    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    /**
     * Service to get a product by its ID
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Service to create a new product (only for admin)
     * @param product
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (!"admin@admin.com".equals(email)) {
            //System.out.println("wrong email => admin@admin.com and not " +email);
            return ResponseEntity.status(403).build(); // Forbidden
        }
        //System.out.println("email OK to add => "+email);
        return ResponseEntity.ok(service.save(product));
    }

    /**
     * Service to update a product by using its ID (only for admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (!"admin@admin.com".equals(email)) {
            //System.out.println("wrong email to modify => email is not admin@admin.com but " +email);
            return ResponseEntity.status(403).build();
        }
        //System.out.println("email OK to modify => " +email);
        return service.getById(id).map(existing -> {
            product.setId(id);
            return ResponseEntity.ok(service.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Service to remove a product  from the product list by using its ID (only for admin)
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        if (!"admin@admin.com".equals(email)) {
            return ResponseEntity.status(403).build();
        }
        if (service.getById(id).isEmpty())
            return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
