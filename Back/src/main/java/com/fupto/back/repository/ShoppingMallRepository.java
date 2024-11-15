package com.fupto.back.repository;

import com.fupto.back.entity.ShoppingMall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ShoppingMallRepository extends JpaRepository<ShoppingMall, Long> {
    @Query("SELECT sm FROM ShoppingMall sm WHERE sm.state = true AND " +
            "(:nameType = 'default_name' AND (sm.korName LIKE %:name% OR sm.engName LIKE %:name%) OR " +
            "(:name IS NULL OR " +
            "(:nameType = 'korName' AND sm.korName LIKE %:name%) OR " +
            "(:nameType = 'engName' AND sm.engName LIKE %:name%))) " +
            "AND (:active IS NULL OR sm.active = :active) " +
            "AND (:startDate IS NULL OR " +
            "(:dateType = 'reg' AND sm.createDate >= :startDate) OR " +
            "(:dateType = 'mod' AND sm.updateDate >= :startDate)) " +
            "AND (:endDate IS NULL OR " +
            "(:dateType = 'reg' AND sm.createDate <= :endDate) OR " +
            "(:dateType = 'mod' AND sm.updateDate <= :endDate))")
    Page<ShoppingMall> searchShoppingmalls(
            @Param("name") String name,
            @Param("nameType") String nameType,
            @Param("active") Boolean active,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("dateType") String dateType,
            Pageable pageable
    );

    List<ShoppingMall> findByActiveIsTrueOrderByEngNameAsc();
}
