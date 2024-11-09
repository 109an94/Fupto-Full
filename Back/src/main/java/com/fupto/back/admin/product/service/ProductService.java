package com.fupto.back.admin.product.service;

import com.fupto.back.admin.product.dto.*;

import java.util.List;

public interface ProductService {

    ProductResponseDto getList(ProductSearchDto searchDto);

    List<ProductListDto> getMappingProducts(Long mappingId);

    ProductListDto getById(Long id);

    List<CategorySelectDto> getCategoriesByLevelAndParent(Integer level, Long parentId);

    List<BrandSelectDto> getBrands();

    List<ShoppingMallSelectDto> getShoppingMalls();

    ProductListDto create(ProductListDto productListDto);

    ProductListDto update(ProductListDto productListDto);

    void updateState(Long id);

    ProductListDto updateActive(Long id, Boolean active);

    void delete(Long id);


    void promoteAndDelete(Long id);
}
