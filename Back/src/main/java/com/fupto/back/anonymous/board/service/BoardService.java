package com.fupto.back.anonymous.board.service;

import com.fupto.back.anonymous.board.dto.BoardDto;
import com.fupto.back.anonymous.board.dto.DefaultDto;
import com.fupto.back.anonymous.board.dto.DetailDto;
import com.fupto.back.anonymous.board.dto.SearchDto;

import java.util.List;

public interface BoardService {

    List<BoardDto> findAll();

    DefaultDto userSearch(SearchDto searchDto);

    DetailDto getById(Long id);
}
