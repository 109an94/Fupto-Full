package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.BoardSearchDto;
import com.fupto.back.entity.Board;

import java.util.List;

public interface BoardService {
    List<Board> getBoards();

    List<BoardListDto> getList();

    BoardListDto getBoardById(Long id);

    BoardResponseDto getSearch(BoardSearchDto boardSearchDto);
}
