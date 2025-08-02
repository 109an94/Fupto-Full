package com.fupto.back.admin.brand.service;

import com.fupto.back.admin.brand.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BrandService {
    BrandResponseDto getList(BrandSearchDto brandSearchDto);

    BrandListDto updateActive(Long id, Boolean active);

    BrandListDto createBrand(BrandCreateDto brandCreateDto, MultipartFile file) throws IOException;

    BrandListDto updateState(Long id, boolean b);

    void bulkUpdateState(List<Long> brandIds, boolean b);

    BrandListDto show(Long id);

    BrandListDto update(Long id, BrandUpdateDto brandUpdateDto, MultipartFile file) throws IOException;
}