package com.fupto.back.repository;

import com.fupto.back.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByMemberId(Long id);
    // Long countAllByMemberId(Long id);

    // 특정 사용자의 특정 상품 찜 정보 조회
    // Optional<Favorite> findByMemberIdAndMappingId(Long memberId, Long mappingId);
    // 특정 사용자가 특정 상품을 찜했는지 여부 확인 (state가 true인 것만)
    // boolean existsByMemberIdAndMappingIdAndStateIsTrue(Long memberId, Long
    // mappingId);

    // 특정 사용자가 찜한 상품 중 state가 true인 것들의 mapping_id 목록(마이페이지 찜목록 조회용)
    // List<Long> findMappingIdsByMemberIdAndStateIsTrue(Long memberId);
}
