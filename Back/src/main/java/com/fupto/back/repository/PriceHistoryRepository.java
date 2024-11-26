package com.fupto.back.repository;

import com.fupto.back.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("""
        SELECT p.retailPrice
        FROM Product p
        WHERE p.id = (
            SELECT ph.product.id
            FROM PriceHistory ph
            WHERE ph.createDate = (
                SELECT MAX(ph2.createDate)
                FROM PriceHistory ph2 
                WHERE ph2.product.id = ph.product.id
            )
            AND ph.product.id IN (
                SELECT p2.id
                FROM Product p2
                WHERE p2.mappingId = :mappingId
            )
            AND ph.salePrice = (
                SELECT MIN(ph3.salePrice)
                FROM PriceHistory ph3
                WHERE ph3.createDate = (
                    SELECT MAX(ph4.createDate)
                    FROM PriceHistory ph4 
                    WHERE ph4.product.id = ph3.product.id
                )
                AND ph3.product.id IN (
                    SELECT p3.id
                    FROM Product p3
                    WHERE p3.mappingId = :mappingId
                )
            )
        )
    """)
    Integer findRetailPriceOfLowestPriceProduct(@Param("mappingId") Long mappingId);

    @Query("SELECT MIN(ph.salePrice) FROM PriceHistory ph " +
            "WHERE ph.product.id IN :productIds " +
            "AND ph.createDate = (SELECT MAX(ph2.createDate) FROM PriceHistory ph2 WHERE ph2.product.id = ph.product.id)")
    Optional<Integer> findLowestPriceByProductIdsAndLatestDate(@Param("productIds") List<Long> productIds);
}
