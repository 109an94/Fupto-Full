package com.fupto.back.anonymous.product.controller;

import com.fupto.back.anonymous.product.dto.BrandDto;
import com.fupto.back.anonymous.product.dto.CategoryDto;
import com.fupto.back.anonymous.product.dto.ProductResponseDto;
import com.fupto.back.anonymous.product.dto.ProductSearchDto;
import com.fupto.back.anonymous.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductResponseDto> searchProducts(ProductSearchDto searchDto) {
        return ResponseEntity.ok(productService.searchProducts(searchDto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(required = false) Long parentId
    ) {
        return ResponseEntity.ok(productService.getCategories(parentId));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDto>> getBrands() {
        return ResponseEntity.ok(productService.getBrands());
    }
}