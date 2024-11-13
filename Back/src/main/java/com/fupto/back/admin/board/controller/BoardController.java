package com.fupto.back.admin.board.controller;

import com.fupto.back.admin.board.dto.BoardRequestsDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.SuccessResponseDto;
import com.fupto.back.admin.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/boards")
public class BoardController {

    private final BoardService boardService;

    // 게시물 조회
    @GetMapping("/list")
    public List<BoardResponseDto> getList() {
        return boardService.getList();
    }

    // 게시글 등록
    @PostMapping("/post")
    public BoardResponseDto createPost(
            @RequestBody BoardRequestsDto requestsDto
    ){
        return boardService.createPost(requestsDto);
    }

    // 선택한 게시글 조회
    @GetMapping("/{id}")
    public BoardResponseDto getPost(@PathVariable Long id) {
        return boardService.getPost(id);
    }

    // 선택한 게시글 수정
    @PutMapping("/{id}")
    public BoardResponseDto updatePost(@PathVariable Long id, @RequestBody BoardRequestsDto requestsDto) throws Exception {
        return boardService.updatePost(id, requestsDto);
    }

    // 선택한 게시글 삭제
    @DeleteMapping("{id}")
    public SuccessResponseDto deletePost(@PathVariable Long id, @RequestBody BoardRequestsDto requestsDto) throws Exception {
        return boardService.deletePost(id,requestsDto);
    }


}



