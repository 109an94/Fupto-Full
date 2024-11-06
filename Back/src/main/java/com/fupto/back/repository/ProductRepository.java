package com.fupto.back.repository;

import com.fupto.back.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{

    List<Product> findByProductName(String productName);
    List<Product> findAll();

}