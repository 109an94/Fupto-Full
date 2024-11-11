package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.BoardSearchDto;
import com.fupto.back.entity.Board;

import java.util.List;


public interface BoardService {

//    BoardListDto create(BoardListDto boardListDto);

//    void delete(Long id);

//    BoardResponseDto getList(int page);

//    List<Board> getList();
//    List<BoardListDto> findAllBoards();
//    BoardListDto create(BoardListDto boardListDto);
    List<Board> getList();
    List<BoardListDto> getBoardMappingList();
    BoardResponseDto getBoardList(BoardSearchDto searchDto);
    BoardResponseDto getBoardList(Integer page, Integer size, String keyWord, List<Long> categoryIds);


}
