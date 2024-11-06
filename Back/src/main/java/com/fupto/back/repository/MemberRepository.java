package com.fupto.back.repository;

import com.fupto.back.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAll();

    @Query("select m from Member m " +
            "where (:userId is null or m.userId like %:userId%)"+
            "and (:nickname is null or m.nickname like %:nickname%)")
    Page<Member> searchMember(
            @Param("userId") String userId,
            @Param("nickname") String nickname
            , Pageable pageable);
}
