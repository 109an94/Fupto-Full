package com.fupto.back.anonymous.product.service;


import com.fupto.back.admin.product.dto.CategorySelectDto;
import com.fupto.back.anonymous.product.dto.BrandDto;
import com.fupto.back.anonymous.product.dto.CategoryDto;
import com.fupto.back.anonymous.product.dto.ProductResponseDto;
import com.fupto.back.anonymous.product.dto.ProductSearchDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto searchProducts(ProductSearchDto searchDto);
    List<CategoryDto> getCategories(Long parentId);
    List<BrandDto> getBrands();
}