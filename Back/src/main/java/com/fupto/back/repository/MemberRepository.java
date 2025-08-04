package com.fupto.back.repository;

import com.fupto.back.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAll();
    Member findByUserId(String userId);
    Optional<Member> findById(Long id);
    Optional<Member> findOptionalByUserId(String userId);
    Boolean existsByUserId(String userId);
    Boolean existsByEmail(String email);

    @Query("select m from Member m " +
            "where" +
            "(:role is null or m.role = :role)"+
            "and (:gender is null or m.gender = :gender)"+
            "and (:userId is null or m.userId like %:userId%)"+
            "and (:nickname is null or m.nickname like %:nickname%)"+
            "and (:email is null or m.email like %:email%)"+
            "and (" +
            "(:dateType = 'regDate' and " +
            "(:startDate is null or m.createDate >= :startDate) and" +
            "(:endDate is null or m.createDate <= :endDate))" +
            "or (:dateType = 'updateDate' and" +
            "(:startDate is null or m.updateDate >= :startDate) and" +
            "(:endDate is null or m.updateDate <= :endDate))"+
            "or (:dateType = 'loginDate' and" +
            "(:startDate is null or m.loginDate >= :startDate) and" +
            "(:endDate is null or m.loginDate <= :endDate))"+
            ")")
    Page<Member> searchMember(
            @Param("role") String memberType,
            @Param("gender") String gender,
            @Param("userId") String userId, //searchType 대신 null 을 받으면 패스
            @Param("nickname") String nickname,
            @Param("email") String email,
                String dateType,
                Instant startDate,
                Instant endDate,
             Pageable pageable);
}
