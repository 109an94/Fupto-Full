package com.fupto.back.admin.brand.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fupto.back.admin.brand.dto.*;
import com.fupto.back.admin.brand.service.BrandService;
import com.fupto.back.admin.product.dto.ProductListDto;
import com.fupto.back.admin.product.dto.ProductUpdateRequestDto;
import com.fupto.back.entity.Brand;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @GetMapping("{id}/edit")
    public ResponseEntity<BrandListDto> show(@PathVariable Long id) {

        return ResponseEntity.ok(brandService.show(id));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BrandListDto> updateBrand(
            @PathVariable Long id,
            @RequestPart("brandData") String brandDataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // brandDataJson 로그 찍기
            System.out.println("Received brand data: " + brandDataJson);
            ObjectMapper objectMapper = new ObjectMapper();
            BrandUpdateDto brandUpdateDto = objectMapper.readValue(brandDataJson, BrandUpdateDto.class);

            // 서비스 호출하여 브랜드 업데이트
            BrandListDto updatedBrand = brandService.update(id, brandUpdateDto, file);

            return ResponseEntity.ok(updatedBrand);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<BrandListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(brandService.updateActive(id, active));
    }

    @PatchMapping("{id}/state")
    public ResponseEntity<BrandListDto> updateBrandState(@PathVariable("id") Long id) {
        try {
            BrandListDto updatedBrand = brandService.updateState(id, false);
            return ResponseEntity.ok(updatedBrand);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/bulk-update-state")
    public ResponseEntity<Void> bulkUpdateBrandState(@RequestBody List<Long> brandIds) {
        try {
            brandService.bulkUpdateState(brandIds, false);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
