package com.fupto.fupto.admin.board.dto;

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
    private long totalCount;
    private long totalPages;
    private boolean nextPage;
    private boolean prevPage;
    private List<Long> pages;
    private List<BoardListDto> boards;
}
