package com.fupto.back.repository;

import com.fupto.back.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByMappingIdAndStateIsTrue(Long mappingId);

    @Query("SELECT p FROM Product p WHERE p.mappingId = :mappingId AND p.state = true AND p.presentId = false")
    List<Product> findAllByMappingIdAndStateTrue(@Param("mappingId") Long mappingId);

    @Query("""
    SELECT p FROM Product p WHERE 
        (:category1 IS NULL OR p.category.parent.parent.id = :category1)
        AND (:category2 IS NULL OR p.category.parent.id = :category2) 
        AND (:category3 IS NULL OR p.category.id = :category3)
        AND (:active IS NULL OR p.active = :active)
        AND (:presentId IS NULL OR p.presentId = :presentId)
        AND (:state IS NULL OR p.state = :state)
        AND (:searchType IS NULL OR :searchKeyword IS NULL OR 
            (:searchType = 'product_name' AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))) OR
            (:searchType = 'brand_name' AND (LOWER(p.brand.korName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR LOWER(p.brand.engName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))) OR
            (:searchType = 'shop_mall' AND (LOWER(p.shoppingMall.korName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) OR LOWER(p.shoppingMall.engName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))))
        AND (:minPrice IS NULL OR EXISTS (SELECT ph FROM PriceHistory ph WHERE ph.product = p AND ph.createDate = (SELECT MAX(ph2.createDate) FROM PriceHistory ph2 WHERE ph2.product = p) AND ph.salePrice >= :minPrice))
        AND (:maxPrice IS NULL OR EXISTS (SELECT ph FROM PriceHistory ph WHERE ph.product = p AND ph.createDate = (SELECT MAX(ph2.createDate) FROM PriceHistory ph2 WHERE ph2.product = p) AND ph.salePrice <= :maxPrice))
        AND (:startDate IS NULL OR :dateType IS NULL OR 
            (:dateType = 'create_date' AND function('DATE', p.createDate) >= function('DATE', :startDate)) OR
            (:dateType = 'update_date' AND function('DATE', p.updateDate) >= function('DATE', :startDate)))
        AND (:endDate IS NULL OR :dateType IS NULL OR 
            (:dateType = 'create_date' AND function('DATE', p.createDate) <= function('DATE', :endDate)) OR
            (:dateType = 'update_date' AND function('DATE', p.updateDate) <= function('DATE', :endDate)))
    """)
    Page<Product> findBySearchCriteria(
            Long category1,
            Long category2,
            Long category3,
            Boolean active,
            Boolean presentId,
            Boolean state,
            String searchType,
            String searchKeyword,
            Integer minPrice,
            Integer maxPrice,
            String dateType,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    );
}
