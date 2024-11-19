package com.fupto.back.repository;

import com.fupto.back.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    @Query("SELECT ph.salePrice FROM PriceHistory ph WHERE ph.product.id = :productId ORDER BY ph.createDate DESC LIMIT 1")
    Integer findLatestPriceByProductId(Long productId);

    @Query("""
    SELECT MIN(ph.salePrice)
    FROM PriceHistory ph
    WHERE ph.createDate = (
        SELECT MAX(ph2.createDate)
        FROM PriceHistory ph2 
        WHERE ph2.product.id = ph.product.id
    )
    AND ph.product.id IN (
        SELECT p.id
        FROM Product p
        WHERE p.mappingId = :mappingId
    )
""")
    Integer findLowestCurrentPrice(@Param("mappingId") Long mappingId);
}
