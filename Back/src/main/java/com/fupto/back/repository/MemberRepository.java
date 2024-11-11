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
            "where" +
            "(:role is null or m.role = :role)"+
            "and (:gender is null or m.gender = :gender)"+
            "and (:userId is null or m.userId like %:userId%)"+
            "and (:nickname is null or m.nickname like %:nickname%)"+
            "and (:email is null or m.email like %:email%)"
    )
    Page<Member> searchMember(
            @Param("role") String memberType,
            @Param("gender") String gender,
            @Param("userId") String userId,
            @Param("nickname") String nickname,
            @Param("email") String email,
             Pageable pageable);
}
