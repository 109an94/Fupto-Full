package com.fupto.back.anonymous.product.service;


import com.fupto.back.anonymous.product.dto.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponseDto searchProducts(ProductSearchDto searchDto);
    List<ProductCateDto> getCategories(Long parentId);
    List<ProductBrandDto> getBrands();
    Resource getProductImages(Long id, Integer displayOrder) throws IOException;
    ProductDetailDto getById(Long id);
    ProductDetailDto getSingleById(Long id);

    ProductResponseDto getAllProductsByShoppingmall(ProductSearchDto searchDto);
}