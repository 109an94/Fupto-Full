package com.fupto.back.admin.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandSearchDto {

    private Integer page = 1;                    // 페이지 번호 (기본값 1)
    private Integer size = 10;                    // 페이지 크기 (기본값 10)
    private String nameType;                 // 이름 종류 (korName, engName 선택)
    private String name;                     // 브랜드명
    private Boolean active;                  // 진열 상태 (진열, 미진열)
    private String dateType;                 // 날짜 종류 (등록일, 수정일 선택)
    private String startDate;               // 검색 시작 날짜
    private String endDate;                 // 검색 종료 날짜
    private String quickDate;                // 빠른 날짜 선택 (어제, 오늘, 이번 주, 이번 달)
    private String sortBy = "createDate";    // 정렬 기준 (기본값 등록일)
    private String sortOrder = "desc";       // 정렬 순서 (기본값 내림차순)
}
