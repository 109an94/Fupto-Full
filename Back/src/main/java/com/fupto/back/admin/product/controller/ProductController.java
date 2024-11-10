package com.fupto.back.admin.product.controller;

import com.fupto.back.admin.product.dto.*;
import com.fupto.back.admin.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("adminProductController")
@RequestMapping("admin/products")
@Slf4j
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductResponseDto> getList(ProductSearchDto searchDto) {
        System.out.println("startDate: " + searchDto.getStartDate());
        System.out.println("endDate: " + searchDto.getEndDate());
        return ResponseEntity.ok(productService.getList(searchDto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategorySelectDto>> getCategories(
            @RequestParam Integer level,
            @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(productService.getCategoriesByLevelAndParent(level, parentId));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandSelectDto>> getBrands() {
        return ResponseEntity.ok(productService.getBrands());
    }

    @GetMapping("/shopping-malls")
    public ResponseEntity<List<ShoppingMallSelectDto>> getShoppingMalls() {
        return ResponseEntity.ok(productService.getShoppingMalls());
    }

    @GetMapping("{id}/mapping")
    public ResponseEntity<List<ProductListDto>> getMappingProducts(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getMappingProducts(id));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long id) throws IOException {
        Resource imageResource = productService.getProductImage(id);
        String contentType = Files.probeContentType(
                Paths.get(imageResource.getURI())
        );

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageResource);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductListDto> show(
            @PathVariable("id") Long id){

        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<List<ProductListDto>> create(
            @RequestPart("data") List<ProductRegDto> productRegDtos,
            MultipartHttpServletRequest request) {

        try {
            log.info("Received productRegDtos: {}", productRegDtos);  // 받은 데이터 확인

            Map<Integer, List<MultipartFile>> filesMap = new HashMap<>();
            for (ProductRegDto dto : productRegDtos) {
                List<MultipartFile> files = request.getFiles("files_" + dto.getFormId());
                log.info("Files for product {}: count={}", dto.getFormId(),
                        files != null ? files.size() : "null");  // 파일 개수 확인
                filesMap.put(dto.getFormId(), files);
            }

            return ResponseEntity.ok(productService.create(productRegDtos, filesMap));
        } catch (Exception e) {
            log.error("Error creating products", e);  // 에러 상세 로그
            throw e;
        }
    }

    // 일반 상품 삭제
    @PatchMapping("{id}/state")
    public ResponseEntity<String> updateState(@PathVariable("id") Long id) {
        productService.updateState(id);
        return ResponseEntity.ok("성공적으로 처리되었습니다.");
    }

    // 대표상품 삭제 시 첫번째 매핑상품을 대표상품으로 승격
    @PatchMapping("{id}/promote")
    public ResponseEntity<String> promoteAndDelete(@PathVariable("id") Long id) {
        productService.promoteAndDelete(id);
        return ResponseEntity.ok("성공적으로 처리되었습니다.");
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<ProductListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(productService.updateActive(id, active));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        return ResponseEntity.ok("성공적으로 삭제되었습니다.");
    }

}
