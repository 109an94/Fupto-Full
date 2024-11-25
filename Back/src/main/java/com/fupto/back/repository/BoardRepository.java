package com.fupto.back.repository;


import com.fupto.back.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select b from Board b where b.regMember.id = :id")
    List<Board> findByRegMemberId(Long id);
    List<Board> findAllByActiveIsTrue();
    List<Board> findAllByOrderByModifiedAtDesc();
    List<Board> findAllByRegMemberIdAndActiveIsTrue(Long id);

    void deleteById(Long id);

    @Query("SELECT b FROM Board b WHERE " +
            "(:searchType IS NULL OR " +
            "(:searchKeyWord IS NULL OR " +
            "(:searchType = 'title' AND b.title LIKE %:searchKeyWord%) OR " +
            "(:searchType = 'regMemberNickName' AND b.regMember.nickname LIKE %:searchKeyWord%) OR " +
            "(:searchType = 'contents' AND b.contents LIKE %:searchKeyWord%))) " +
            "AND (:active IS NULL OR b.active = :active) " +
            "AND (:boardCategory IS NULL OR b.boardCategory.name = :boardCategory) " +
            "AND (:startDate IS NULL OR " +
            "(:dateType = 'cre' AND b.createdAt >= :startDate) OR " +
            "(:dateType = 'mod' AND b.modifiedAt >= :startDate)) " +
            "AND (:endDate IS NULL OR " +
            "(:dateType = 'cre' AND b.createdAt <= :endDate) OR " +
            "(:dateType = 'mod' AND b.modifiedAt <= :endDate))")
    Page<Board> searchBoards(
            @Param("searchKeyWord") String searchKeyWord,
            @Param("searchType") String searchType,
            @Param("active") Boolean active,
            @Param("boardCategory") String boardCategory,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("dateType") String dateType,
            Pageable pageable);

    List<Board> findByBoardCategoryId(Long boardCategoryId);

//    @Query("SELECT b FROM Board b WHERE " +
//            "(:searchType IS NULL OR " +
//            "(:searchKeyWord IS NULL OR " +
//            "(:searchType = 'title' AND b.title LIKE %:searchKeyWord%) OR " +
//            "(:searchType = 'regMemberNickName' AND b.regMember.nickname LIKE %:searchKeyWord%) OR " +
//            "(:searchType = 'contents' AND b.contents LIKE %:searchKeyWord%))) " +
//            "AND (:boardCategory IS NULL OR b.boardCategory.name = :boardCategory) ")
//    Page<Board> userSearchBoards(
//            @Param("searchKeyWord") String searchKeyWord,
//            @Param("searchType") String searchType,
//            @Param("boardCategory") String boardCategory,
//            Pageable pageable);

    @Query("SELECT b FROM Board b WHERE " +
            "(:searchType IS NULL OR " +
            "(:searchKeyWord IS NULL OR " +
            "(:searchType = 'title' AND b.title LIKE %:searchKeyWord%) OR " +
            "(:searchType = 'regMemberNickName' AND b.regMember.nickname LIKE %:searchKeyWord%) OR " +
            "(:searchType = 'contents' AND b.contents LIKE %:searchKeyWord%))) " +
            "AND (:boardCategory IS NULL OR b.boardCategory.name = :boardCategory) " +
            "AND b.active = true")  // active 필터링 추가
    Page<Board> userSearchBoards(
            @Param("searchKeyWord") String searchKeyWord,
            @Param("searchType") String searchType,
            @Param("boardCategory") String boardCategory,
            Pageable pageable);
}

