package com.fupto.back.repository;

import com.fupto.back.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    @Query("SELECT ph.salePrice FROM PriceHistory ph WHERE ph.product.id = :productId ORDER BY ph.createDate DESC LIMIT 1")
    Integer findLatestPriceByProductId(Long productId);
}
