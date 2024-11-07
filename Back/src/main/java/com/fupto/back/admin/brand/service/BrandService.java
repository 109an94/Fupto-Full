package com.fupto.back.admin.brand.service;

import com.fupto.back.admin.brand.dto.BrandResponseDto;
import com.fupto.back.admin.brand.dto.BrandSearchDto;

public interface BrandService {
    BrandResponseDto getList(BrandSearchDto brandSearchDto);
}