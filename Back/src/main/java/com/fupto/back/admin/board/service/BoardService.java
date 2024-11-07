package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.BoardSearchDto;

import java.util.List;


public interface BoardService {

//    BoardListDto create(BoardListDto boardListDto);

//    void delete(Long id);

//    BoardResponseDto getList(int page);

//    List<Board> getList();
//    List<BoardListDto> findAllBoards();

    BoardResponseDto getList(BoardSearchDto searchDto);

    BoardResponseDto getList(Integer page, String korName, List<Long> categoryIds);

    BoardListDto create(BoardListDto boardListDto);
}
