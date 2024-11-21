package com.fupto.back.anonymous.shoppingmall.service;

import com.fupto.back.anonymous.shoppingmall.dto.ShoppingmallDetailDto;
import com.fupto.back.anonymous.shoppingmall.dto.ShoppingmallListDto;

import java.util.List;

public interface ShoppingmallSerivce {

    List<ShoppingmallListDto> findAll();

    ShoppingmallDetailDto getById(Long id);
}
