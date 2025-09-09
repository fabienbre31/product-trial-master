package com.example.product.service;

import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    /**
     * get the complete list of products in database
     * @return
     */
    public List<Product> getAll() {
        return repo.findAll();
    }

    /**
     * get a product by its ID if it exists, or empty
     * @param id
     * @return
     */
    public Optional<Product> getById(Long id) {
        return repo.findById(id);
    }

    /**
     * save a product
     * @param product
     * @return
     */
    public Product save(Product product) {
        return repo.save(product);
    }

    /**
     * delete a product by its ID
     * @param id
     */
    public void delete(Long id) {
        repo.deleteById(id);
    }

}