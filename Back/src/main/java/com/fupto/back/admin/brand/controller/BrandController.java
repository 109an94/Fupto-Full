package com.fupto.back.admin.brand.controller;

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
    public ResponseEntity<BrandListDto> create(
            @RequestPart("brandData") BrandCreateDto brandCreateDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(brandService.create(brandCreateDto, file));
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<BrandListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(brandService.updateActive(id, active));
    }
}
