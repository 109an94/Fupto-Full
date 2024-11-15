package com.fupto.back.anonymous.product.service;


import com.fupto.back.admin.product.dto.CategorySelectDto;
import com.fupto.back.anonymous.product.dto.BrandDto;
import com.fupto.back.anonymous.product.dto.CategoryDto;
import com.fupto.back.anonymous.product.dto.ProductResponseDto;
import com.fupto.back.anonymous.product.dto.ProductSearchDto;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponseDto searchProducts(ProductSearchDto searchDto);
    List<CategoryDto> getCategories(Long parentId);
    List<BrandDto> getBrands();

    Resource getProductImages(Long id, Integer displayOrder) throws IOException;
}