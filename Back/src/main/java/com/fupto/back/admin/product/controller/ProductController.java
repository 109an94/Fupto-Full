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

    @GetMapping("/{id}/image/{order}")
    public ResponseEntity<Resource> getProductImage(
            @PathVariable Long id,
            @PathVariable Integer order) throws IOException {
        Resource imageResource = productService.getProductImage(id, order);
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

    @GetMapping("/{id}/edit")
    public ResponseEntity<ProductUpdateDto> getProductForEdit(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductForEdit(id));
    }

    @PostMapping
    public ResponseEntity<List<ProductListDto>> create(
            @RequestPart("data") List<ProductRegDto> productRegDtos,
            MultipartHttpServletRequest request) {
        try {
            Map<Integer, List<MultipartFile>> filesMap = new HashMap<>();
            Map<Integer, Map<String, MultipartFile>> filesByNameMap = new HashMap<>();

            for (ProductRegDto dto : productRegDtos) {
                List<MultipartFile> files = request.getFiles("files_" + dto.getFormId());
                filesMap.put(dto.getFormId(), files);

                Map<String, MultipartFile> fileMap = new HashMap<>();
                if (files != null) {
                    for (MultipartFile file : files) {
                        fileMap.put(file.getOriginalFilename(), file);
                    }
                }
                filesByNameMap.put(dto.getFormId(), fileMap);
            }

            return ResponseEntity.ok(productService.create(productRegDtos, filesMap, filesByNameMap));
        } catch (Exception e) {
            log.error("Error creating products", e);
            throw e;
        }
    }

    @PostMapping("/mapping")
    public ResponseEntity<?> createMapping(@RequestBody ProductMappingDto request) {
        productService.createMapping(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductListDto> update(
            @PathVariable Long id,
            @RequestPart("data") ProductUpdateRequestDto updateDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        return ResponseEntity.ok(productService.update(id, updateDto, files));
    }

    @PatchMapping("{id}/state")
    public ResponseEntity<String> updateState(@PathVariable("id") Long id) {
        productService.updateState(id);
        return ResponseEntity.ok("성공적으로 처리되었습니다.");
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<ProductListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(productService.updateActive(id, active));
    }

    @PatchMapping("{id}/promote")
    public ResponseEntity<String> promoteAndDelete(@PathVariable("id") Long id) {
        productService.promoteAndDelete(id);
        return ResponseEntity.ok("성공적으로 처리되었습니다.");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        return ResponseEntity.ok("성공적으로 삭제되었습니다.");
    }

}
