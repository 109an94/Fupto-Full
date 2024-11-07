package com.fupto.back.admin.brand.controller;

import com.fupto.back.admin.brand.dto.BrandResponseDto;
import com.fupto.back.admin.brand.dto.BrandSearchDto;
import com.fupto.back.admin.brand.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminBrandController")
@RequestMapping("admin/brands")
public class BrandController {

    private BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<BrandResponseDto> getList(@ModelAttribute BrandSearchDto brandSearchDto){
        System.out.println(brandSearchDto.toString());
        return ResponseEntity.ok(brandService.getList(brandSearchDto));
    }
}
