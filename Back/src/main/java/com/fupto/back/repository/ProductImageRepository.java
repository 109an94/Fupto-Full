package com.fupto.back.repository;

import com.fupto.back.entity.Product;
import com.fupto.back.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    Optional<ProductImage> findByProductIdAndDisplayOrder(Long productId, Integer displayOrder);
}
