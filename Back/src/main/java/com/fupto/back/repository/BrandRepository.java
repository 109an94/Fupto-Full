package com.fupto.back.repository;

import com.fupto.back.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT b FROM Brand b WHERE b.state = true AND " +
            "(:nameType = 'default_name' AND (b.korName LIKE %:name% OR b.engName LIKE %:name%) OR " +
            "(:name IS NULL OR " +
            "(:nameType = 'korName' AND b.korName LIKE %:name%) OR " +
            "(:nameType = 'engName' AND b.engName LIKE %:name%))) " +
            "AND (:active IS NULL OR b.active = :active) " +
            "AND (:startDate IS NULL OR " +
            "(:dateType = 'reg' AND b.createDate >= :startDate) OR " +
            "(:dateType = 'mod' AND b.updateDate >= :startDate)) " +
            "AND (:endDate IS NULL OR " +
            "(:dateType = 'reg' AND b.createDate <= :endDate) OR " +
            "(:dateType = 'mod' AND b.updateDate <= :endDate))")
    Page<Brand> searchBrands(
            @Param("name") String name,
            @Param("nameType") String nameType,
            @Param("active") Boolean active,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("dateType") String dateType,
            Pageable pageable
    );


    List<Brand> findByActiveIsTrueOrderByEngNameAsc();
}
