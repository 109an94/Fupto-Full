package com.fupto.back.admin.board.dto;

import com.fupto.back.admin.brand.dto.BrandListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {
    private Long totalElements;
    private Long totalPages;
    private Boolean hasNextPage;
    private Boolean hasPreviousPage;
    private List<Long> pages;
    private List<BoardListDto> boards;
}
