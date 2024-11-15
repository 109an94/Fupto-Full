package com.fupto.back.admin.shoppingmall.service;

import com.fupto.back.admin.shoppingmall.dto.ShoppingmallCreateDto;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallListDto;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallResponseDto;
import com.fupto.back.admin.shoppingmall.dto.ShoppingmallSearchDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ShoppingmallSerivce {
    ShoppingmallResponseDto getList(ShoppingmallSearchDto shoppingmallSearchDto);

    ShoppingmallListDto updateActive(Long id, Boolean active);

    ShoppingmallListDto createShoppingmall(ShoppingmallCreateDto shoppingmallCreateDto, MultipartFile file) throws IOException;

    ShoppingmallListDto updateState(Long id, boolean b);

    void bulkUpdateState(List<Long> shoppingmallIds, boolean b);
}
