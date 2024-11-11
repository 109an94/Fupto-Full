package com.fupto.back.repository;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAll();

    @Query("SELECT b FROM Board b " +
            "WHERE (:title IS NULL OR b.title LIKE %:title%) " +
            "AND (:regMemberId IS NULL OR b.regMember = :regMember)")
    Page<Board> findByTitleAndRegMemberId(
            @Param("title") String title,
            @Param("regMemberId") Long regMember,
            Pageable pageable);
}
