package com.fupto.back.admin.product.service;

import com.fupto.back.admin.product.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductResponseDto getList(ProductSearchDto searchDto);

    List<ProductListDto> getMappingProducts(Long mappingId);

    ProductListDto getById(Long id);

    List<CategorySelectDto> getCategoriesByLevelAndParent(Integer level, Long parentId);

    List<BrandSelectDto> getBrands();

    List<ShoppingMallSelectDto> getShoppingMalls();

    Resource getProductImage(Long id) throws IOException;

    List<ProductListDto> create(List<ProductRegDto> productRegDtos, Map<Integer, List<MultipartFile>> filesMap);

    ProductListDto update(ProductListDto productListDto);

    void updateState(Long id);

    ProductListDto updateActive(Long id, Boolean active);

    void delete(Long id);

    void promoteAndDelete(Long id);
}
