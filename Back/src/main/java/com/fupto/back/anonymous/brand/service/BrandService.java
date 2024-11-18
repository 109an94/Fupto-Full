package com.fupto.back.anonymous.brand.service;

import com.fupto.back.anonymous.brand.dto.BrandDetailDto;
import com.fupto.back.anonymous.brand.dto.BrandDto;

import java.util.List;

public interface BrandService {
    List<BrandDto> findAll();

    BrandDetailDto getById(Long id);
}
