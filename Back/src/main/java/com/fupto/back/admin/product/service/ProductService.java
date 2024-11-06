package com.fupto.back.admin.product.service;

import com.fupto.back.admin.product.dto.CategorySelectDto;
import com.fupto.back.admin.product.dto.ProductListDto;
import com.fupto.back.admin.product.dto.ProductResponseDto;
import com.fupto.back.admin.product.dto.ProductSearchDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto getList(ProductSearchDto searchDto);

    List<ProductListDto> getMappingProducts(Long mappingId);

    ProductListDto getById(Long id);

    ProductListDto create(ProductListDto productListDto);

    ProductListDto update(ProductListDto productListDto);

    void updateState(Long id);

    ProductListDto updateActive(Long id, Boolean active);

    void delete(Long id);

    List<CategorySelectDto> getCategoriesByLevelAndParent(Integer level, Long parentId);

    void promoteAndDelete(Long id);
}
