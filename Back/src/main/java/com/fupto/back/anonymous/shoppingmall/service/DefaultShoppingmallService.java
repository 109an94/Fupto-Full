package com.fupto.back.anonymous.shoppingmall.service;

import com.fupto.back.anonymous.shoppingmall.dto.ShoppingmallDetailDto;
import com.fupto.back.anonymous.shoppingmall.dto.ShoppingmallListDto;
import com.fupto.back.entity.ShoppingMall;
import com.fupto.back.repository.ShoppingMallRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultShoppingmallService implements ShoppingmallSerivce{

    private ShoppingMallRepository shoppingMallRepository;

    public DefaultShoppingmallService(ShoppingMallRepository shoppingMallRepository) {
        this.shoppingMallRepository = shoppingMallRepository;
    }

    @Override
    public List<ShoppingmallListDto> findAll() {
        return shoppingMallRepository.findByActiveIsTrueOrderByEngNameAsc()
                .stream()
                .map(shoppingMall -> ShoppingmallListDto.builder()
                        .id(shoppingMall.getId())
                        .korName(shoppingMall.getKorName())
                        .engName(shoppingMall.getEngName())
                        .build())
                .toList();
    }

    @Override
    public ShoppingmallDetailDto getById(Long id) {
        ShoppingMall shoppingMall = shoppingMallRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shoppingmall not found"));
        return ShoppingmallDetailDto.builder()
                .id(shoppingMall.getId())
                .korName(shoppingMall.getKorName())
                .engName(shoppingMall.getEngName())
                .description(shoppingMall.getDescription())
                .img(shoppingMall.getImg())
                .build();
    }
}
