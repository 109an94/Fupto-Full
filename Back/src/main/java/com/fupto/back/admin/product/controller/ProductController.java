package com.fupto.back.admin.product.controller;

import com.fupto.back.admin.product.dto.CategorySelectDto;
import com.fupto.back.admin.product.dto.BrandSelectDto;
import com.fupto.back.admin.product.dto.ShoppingMallSelectDto;
import com.fupto.back.admin.product.dto.ProductListDto;
import com.fupto.back.admin.product.dto.ProductResponseDto;
import com.fupto.back.admin.product.dto.ProductSearchDto;
import com.fupto.back.admin.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminProductController")
@RequestMapping("admin/products")
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

    @GetMapping("{id}")
    public ResponseEntity<ProductListDto> show(
            @PathVariable("id") Long id){

        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductListDto> create(@RequestBody ProductListDto productListDto){
        return ResponseEntity.ok(productService.create(productListDto));
    }

    // 일반 상품 삭제
    @PatchMapping("{id}/state")
    public ResponseEntity<String> updateState(@PathVariable("id") Long id) {
        productService.updateState(id);
        return ResponseEntity.ok("성공적으로 처리되었습니다.");
    }

    // 대표상품 삭제 시 첫번째 매핑상품을 대표상품으로 승격시키는 엔드포인트
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
