package com.fupto.back.repository;

import com.fupto.back.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
    Optional<BoardCategory> findById(Long id);
}
