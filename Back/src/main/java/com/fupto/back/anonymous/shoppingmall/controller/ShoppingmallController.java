package com.fupto.back.anonymous.shoppingmall.controller;

import com.fupto.back.anonymous.shoppingmall.dto.ShoppingmallDetailDto;
import com.fupto.back.anonymous.shoppingmall.dto.ShoppingmallListDto;
import com.fupto.back.anonymous.shoppingmall.service.ShoppingmallSerivce;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("shoppingmalls")
public class ShoppingmallController {

    private ShoppingmallSerivce shoppingmallService;

    public ShoppingmallController(ShoppingmallSerivce shoppingmallService) {
        this.shoppingmallService = shoppingmallService;
    }

    @GetMapping
    public ResponseEntity<List<ShoppingmallListDto>> findAll() {
        return ResponseEntity.ok(shoppingmallService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingmallDetailDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shoppingmallService.getById(id));
    }

}
