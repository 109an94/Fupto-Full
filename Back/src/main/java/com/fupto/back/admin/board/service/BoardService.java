package com.fupto.fupto.admin.board.service;

import com.fupto.fupto.admin.board.dto.BoardListDto;
import com.fupto.fupto.admin.board.dto.BoardResponseDto;
import com.fupto.fupto.admin.board.dto.BoardSearchDto;
import com.fupto.fupto.entity.Board;

import java.util.List;


public interface BoardService {

//    BoardListDto create(BoardListDto boardListDto);

//    void delete(Long id);

//    BoardResponseDto getList(int page);

//    List<Board> getList();
//    List<BoardListDto> findAllBoards();

    BoardResponseDto getList(BoardSearchDto searchDto);

    BoardResponseDto getList(Integer page, String korName, List<Long> categoryIds);
}
