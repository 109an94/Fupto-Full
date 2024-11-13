package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.BoardRequestsDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.SuccessResponseDto;

import java.util.List;


public interface BoardService {

    List<BoardResponseDto> getList();

    BoardResponseDto createPost(BoardRequestsDto requestsDto);

    BoardResponseDto getPost(Long id);

    BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception;

    SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception;
}
