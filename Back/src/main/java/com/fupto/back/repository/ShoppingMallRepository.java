package com.fupto.back.repository;

import com.fupto.back.entity.ShoppingMall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingMallRepository extends JpaRepository<ShoppingMall, Long> {
    List<ShoppingMall> findByActiveIsTrue();
}
