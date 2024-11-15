package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface BoardService {


    // 전체 조회
    List<BoardListDto> getList();
//    List<BoardResponseDto> getList();

    // id 조회
    BoardDetailDto getBoardById(Long id);
//    BoardResponseDto getPost(Long id);

    // 등록
    BoardListDto createPost(BoardListDto boardListDto);
//    BoardResponseDto createPost(BoardRequestsDto requestsDto);

    // 수정
    BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception;

    // 삭제
    SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception;

    BoardDefaultDto getSearch(BoardSearchDto boardSearchDto);


}
