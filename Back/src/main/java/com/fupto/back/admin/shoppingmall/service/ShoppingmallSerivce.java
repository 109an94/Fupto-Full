package com.fupto.back.admin.shoppingmall.service;

import com.fupto.back.admin.shoppingmall.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ShoppingmallSerivce {
    ShoppingmallResponseDto getList(ShoppingmallSearchDto shoppingmallSearchDto);

    ShoppingmallListDto updateActive(Long id, Boolean active);

    ShoppingmallListDto createShoppingmall(ShoppingmallCreateDto shoppingmallCreateDto, MultipartFile file) throws IOException;

    ShoppingmallListDto updateState(Long id, boolean b);

    void bulkUpdateState(List<Long> shoppingmallIds, boolean b);

    ShoppingmallListDto show(Long id);

    ShoppingmallListDto update(Long id, ShoppingmallUpdateDto shoppingmallUpdateDto, MultipartFile file) throws IOException;
}
