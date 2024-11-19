package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.admin.brand.dto.BrandCreateDto;
import com.fupto.back.admin.brand.dto.BrandListDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface BoardService {


    // 전체 조회
    List<BoardListDto> getList();
//    List<BoardResponseDto> getList();

    // id 조회
    BoardDetailDto getBoardById(Long id);
//    BoardResponseDto getPost(Long id);
    BoardDefaultDto getSearch(BoardSearchDto boardSearchDto);

    // 등록
    BoardListDto createPost(BoardListDto boardListDto);

//    BoardCreateDto createBoard(BoardCreateDto boardCreateDto);

//    BoardListDto createBoard(BoardCreateDto boardCreateDto) throws IOException;

    BoardListDto createBoard(BoardCreateDto boardCreateDto, MultipartFile file) throws IOException;

    // 수정
    BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception;

    // 삭제
//    SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception;
    SuccessResponseDto deletePost(Long id) throws Exception;
    void deleteSelected(List<Long> ids);

    // 엑티브 수정
    BoardListDto updateActive(Long id, Boolean active);


    //
}
