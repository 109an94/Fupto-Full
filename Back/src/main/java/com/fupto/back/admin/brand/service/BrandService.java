package com.fupto.back.admin.brand.service;

import com.fupto.back.admin.brand.dto.BrandCreateDto;
import com.fupto.back.admin.brand.dto.BrandListDto;
import com.fupto.back.admin.brand.dto.BrandResponseDto;
import com.fupto.back.admin.brand.dto.BrandSearchDto;
import org.springframework.web.multipart.MultipartFile;

public interface BrandService {
    BrandResponseDto getList(BrandSearchDto brandSearchDto);

    BrandListDto updateActive(Long id, Boolean active);

    BrandListDto create(BrandCreateDto brandCreateDto, MultipartFile file);
}