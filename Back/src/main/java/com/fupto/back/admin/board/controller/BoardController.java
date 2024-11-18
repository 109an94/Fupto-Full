package com.fupto.back.admin.board.controller;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.admin.board.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequiredArgsConstructor
@RequestMapping("admin/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 전체 게시물 조회
    @GetMapping("/list")
    public List<BoardListDto> getList() {
        return boardService.getList();
    }

    // 선택한 게시글 조회
    @GetMapping("{id}")
    public ResponseEntity<BoardDetailDto> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    // 게시글 검색 조회
    @GetMapping
    public ResponseEntity<BoardDefaultDto> searchBoard(
            @ModelAttribute BoardSearchDto boardSearchDto
    ) {
        return ResponseEntity.ok(boardService.getSearch(boardSearchDto));
    }

    // 게시글 등록
    @PostMapping("/post")
    public ResponseEntity<BoardListDto> createPost( @RequestBody BoardListDto boardListDto){

        BoardListDto createBoard = boardService.createPost(boardListDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createBoard);
    }

    // 선택한 게시글 수정
    @PutMapping("/{id}")
    public BoardResponseDto updatePost(@PathVariable Long id, @RequestBody BoardRequestsDto requestsDto) throws Exception {
        return boardService.updatePost(id, requestsDto);
    }

    // 게시글 삭제

    @DeleteMapping("{id}")
    public SuccessResponseDto deletePost(@PathVariable Long id) throws Exception {
        return boardService.deletePost(id);
    }
//    @DeleteMapping("{id}") 비밀번호 추가
//    public SuccessResponseDto deletePost(@PathVariable Long id, @RequestBody BoardRequestsDto requestsDto) throws Exception {
//        return boardService.deletePost(id,requestsDto);
//    }

    @DeleteMapping("/selected")
    public ResponseEntity<Void> deleteSelected(@RequestBody List<Long> ids){
      try{
          boardService.deleteSelected(ids);
          return ResponseEntity.noContent().build();
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    }

    // 액티브 변경
    @PatchMapping("{id}/active")
    public ResponseEntity<BoardListDto> updateActive(
            @PathVariable("id") Long id,
            @RequestParam Boolean active) throws Exception {
        return ResponseEntity.ok(boardService.updateActive(id, active));
    }
}



