package com.fupto.back.repository;

import com.fupto.back.entity.Favorite;
import com.fupto.back.user.member.dto.FavoriteListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByMemberId(Long id);
    List<Favorite> findAllByMemberIdAndStateIsTrue(Long id);
//    Optional<Favorite> findByIdAndMemberId(Long id, Long memberId);

    List<Favorite> findByMappingId(Long productId);

    // Long countAllByMemberId(Long id);

    Optional<Favorite> findByMemberIdAndMappingId(Long memberId, Long mappingId);

    boolean existsByMemberIdAndMappingIdAndStateIsTrue(Long memberId, Long mappingId);
}
