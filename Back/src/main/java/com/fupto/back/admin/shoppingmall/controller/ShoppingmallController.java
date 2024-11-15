package com.fupto.back.admin.shoppingmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallCreateDto;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallListDto;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallResponseDto;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallSearchDto;
import com.fupto.back.admin.shoppingmall.service.ShoppingmallSerivce;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController("adminShoppingmallController")
@RequestMapping("admin/shoppingmalls")
public class ShoppingmallController {

    private ShoppingmallSerivce shoppingmallService;

    public ShoppingmallController(ShoppingmallSerivce shoppingmallSerivce) {
        this.shoppingmallService = shoppingmallSerivce;
    }

    @GetMapping
    public ResponseEntity<ShoppingmallResponseDto> getList(@ModelAttribute ShoppingmallSearchDto shoppingmallSearchDto) {
        System.out.println(shoppingmallSearchDto.toString());
        return ResponseEntity.ok(shoppingmallService.getList(shoppingmallSearchDto));
    }

    @PostMapping(value = "/reg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShoppingmallListDto> registerBrand(
            @RequestPart("shoppingmallData") String shoppingmallDataJson,
            @RequestPart("file") MultipartFile file) {
        try {
            // shoppingmallDataJson 로그 찍기
            System.out.println("Received shoppingmall data: " + shoppingmallDataJson);
            System.out.println(file.getOriginalFilename());
            ObjectMapper objectMapper = new ObjectMapper();
            ShoppingmallCreateDto shoppingmallCreateDto = objectMapper.readValue(shoppingmallDataJson, ShoppingmallCreateDto.class);
            System.out.println(shoppingmallCreateDto.toString());
            ShoppingmallListDto createdShoppingmall = shoppingmallService.createShoppingmall(shoppingmallCreateDto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdShoppingmall);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<ShoppingmallListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(shoppingmallService.updateActive(id, active));
    }

    @PatchMapping("{id}")
    public ResponseEntity<ShoppingmallListDto> updateShoppingmallState(@PathVariable("id") Long id) {
        try {
            ShoppingmallListDto updatedShoppingmall = shoppingmallService.updateState(id, false);
            return ResponseEntity.ok(updatedShoppingmall);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/bulk-update-state")
    public ResponseEntity<Void> bulkUpdateBShoppingmallState(@RequestBody List<Long> shoppingmallIds) {
        try {
            shoppingmallService.bulkUpdateState(shoppingmallIds, false);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
