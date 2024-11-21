package com.fupto.back.anonymous.board.dto;

import com.fupto.back.admin.board.dto.BoardListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDefaultDto {
    private Long totalElements;
    private Long totalPages;
    private Boolean hasNextPage;
    private Boolean hasPreviousPage;
    private List<Long> pages;
    private List<BoardListDto> boards;
}
