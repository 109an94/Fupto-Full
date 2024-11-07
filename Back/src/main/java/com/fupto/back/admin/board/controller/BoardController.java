package com.fupto.back.admin.board.controller;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.BoardSearchDto;
import com.fupto.back.admin.board.service.BoardService;
import com.fupto.back.repository.BoardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("admin/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private BoardService boardService;

    public BoardController(BoardService boardService, BoardRepository boardRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

//     등록
    @PostMapping("/reg")
    public ResponseEntity<BoardListDto> create(
            @RequestBody BoardListDto boardListDto) {
        System.out.println(boardListDto);
        return ResponseEntity.ok(boardService.create(boardListDto));
    }

    // 목록 - 페이징
    @GetMapping("/list")
    public ResponseEntity<BoardResponseDto> getList(
            @ModelAttribute BoardSearchDto searchDto
            ) {
        if(searchDto.getPage() == null || searchDto.getPage() < 1) {
            searchDto.setPage(1);
        }
        return ResponseEntity.ok(boardService.getList(searchDto));
    }
}

    //삭제
//    @DeleteMapping
//    public void deleteById(Long id) {
//        boardService.delete(id);
//    }



