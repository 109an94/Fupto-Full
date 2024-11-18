package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
//    BoardListDto createPost(BoardListDto boardListDto);
//    BoardResponseDto createPost(BoardRequestsDto requestsDto);
    BoardListDto createPost(BoardListDto boardListDto);

    // ========== 등록 =========================================================================
//    BoardListDto createPost(@RequestParam("title") String title,
//                            @RequestParam("contents") String contents,
//                            @RequestParam("boardCategoryName") String boardCategoryName,
//                            @RequestParam("file") MultipartFile file,
//                            @RequestParam("boardCategoryId") Long boardCategoryId,
//                            @RequestParam("regMemberId") Long regMemberId);

    // 수정
    BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception;

    // 삭제
//    SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception;
    SuccessResponseDto deletePost(Long id) throws Exception;

    // 엑티브 수정
    BoardListDto updateActive(Long id, Boolean active);


    void deleteSelected(List<Long> ids);
}
