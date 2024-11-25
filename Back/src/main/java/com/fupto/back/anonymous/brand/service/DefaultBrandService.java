package com.fupto.back.anonymous.brand.service;

import com.fupto.back.anonymous.brand.dto.BrandDetailDto;
import com.fupto.back.anonymous.brand.dto.BrandDto;
import com.fupto.back.entity.Brand;
import com.fupto.back.repository.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultBrandService implements BrandService {

    private BrandRepository brandRepository;

    public DefaultBrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<BrandDto> findAll() {
        return brandRepository.findByActiveIsTrueOrderByEngNameAsc()
                .stream()
                .map(brand -> BrandDto.builder()
                        .id(brand.getId())
                        .engName(brand.getEngName())
                        .korName(brand.getKorName())
                        .img(brand.getImg())
                        .build())
                .toList();
    }

    @Override
    public BrandDetailDto getById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));

        return BrandDetailDto.builder()
                .id(brand.getId())
                .korName(brand.getKorName())
                .engName(brand.getEngName())
                .description(brand.getDescription())
                .img(brand.getImg())
                .build();
    }
}
