package com.fupto.back.anonymous.product.service;


import com.fupto.back.anonymous.product.dto.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponseDto searchProducts(ProductSearchDto searchDto, Long memberId);
    List<ProductCateDto> getCategories(Long parentId);
    List<ProductBrandDto> getBrands();
    Resource getProductImages(Long id, Integer displayOrder) throws IOException;
    ProductDetailDto getById(Long id, Long memberId);
    ProductDetailDto getSingleById(Long id, Long memberId);

    ProductResponseDto getAllProductsByShoppingmall(ProductSearchDto searchDto, Long memberId);

    boolean toggleFavorite(Long mappingId, Long memberId);
    boolean isFavorite(Long mappingId, Long memberId);
}