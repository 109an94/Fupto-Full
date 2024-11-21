package com.fupto.back.anonymous.board.service;

import com.fupto.back.anonymous.board.dto.BoardDefaultDto;
import com.fupto.back.anonymous.board.dto.BoardListDto;
import com.fupto.back.anonymous.board.dto.BoardSearchDto;

import java.util.List;


public interface BoardService {

    List<BoardListDto> getAllList();

    BoardDefaultDto getSearch(BoardSearchDto boardSearchDto);

}
