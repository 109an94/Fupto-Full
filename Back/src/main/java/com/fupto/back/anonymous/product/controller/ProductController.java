package com.fupto.back.anonymous.product.controller;

import com.fupto.back.anonymous.product.dto.*;
import com.fupto.back.anonymous.product.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public ResponseEntity<List<ProductCateDto>> getCategories(
            @RequestParam(required = false) Long parentId
    ) {
        return ResponseEntity.ok(productService.getCategories(parentId));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<ProductBrandDto>> getBrands() {
        return ResponseEntity.ok(productService.getBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/{id}/image/{displayOrder}")
    public ResponseEntity<Resource> getProductImage(
            @PathVariable Long id, @PathVariable Integer displayOrder) throws IOException {
        Resource imageResource = productService.getProductImages(id, displayOrder);
        String contentType = Files.probeContentType(
                Paths.get(imageResource.getURI())
        );

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageResource);
    }
}