package com.fupto.back.admin.brand.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fupto.back.admin.brand.dto.BrandCreateDto;
import com.fupto.back.admin.brand.dto.BrandListDto;
import com.fupto.back.admin.brand.dto.BrandResponseDto;
import com.fupto.back.admin.brand.dto.BrandSearchDto;
import com.fupto.back.admin.brand.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/reg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BrandListDto> registerBrand(
            @RequestPart("brandData") String brandDataJson,
            @RequestPart("file") MultipartFile file) {
        try {
            // brandDataJson 로그 찍기
            System.out.println("Received brand data: " + brandDataJson);
            System.out.println(file.getOriginalFilename());
            ObjectMapper objectMapper = new ObjectMapper();
            BrandCreateDto brandCreateDto = objectMapper.readValue(brandDataJson, BrandCreateDto.class);
            System.out.println(brandCreateDto.toString());
            BrandListDto createdBrand = brandService.createBrand(brandCreateDto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<BrandListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(brandService.updateActive(id, active));
    }
}
