package com.fupto.back.repository;

import com.fupto.back.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void joinTest(){
        List<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
    }
}
