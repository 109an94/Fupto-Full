package com.fupto.back.repository;

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

//    @Query("""
//    SELECT b FROM Board b
//    WHERE
//        (:boardCategory IS NULL OR b.boardCategory.name = :boardCategory)
//        AND
//        (:searchKeyword IS NULL OR
//            (:searchType = 'title' AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))) OR
//            (:searchType = 'content' AND LOWER(b.content) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))) OR
//            (:searchType = 'member_nickname' AND LOWER(b.member.nickname) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))
//        )
//    """)
//    Page<Board> searchBoards(
//            @Param("boardCategory") String boardCategory,
//            @Param("searchType") String searchType,
//            @Param("searchKeyword") String searchKeyword,
//            Pageable pageable);
//
//    Page<Board> searchBoard(String boardCategory, String searchType, String searchType1);
}


