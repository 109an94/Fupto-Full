package com.fupto.back.anonymous.brand.controller;

import com.fupto.back.anonymous.brand.dto.BrandDetailDto;
import com.fupto.back.anonymous.brand.dto.BrandDto;
import com.fupto.back.anonymous.brand.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("brands")
public class BrandController {

    private BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDto>> findAll() {
        return ResponseEntity.ok(brandService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDetailDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getById(id));
    }
}
