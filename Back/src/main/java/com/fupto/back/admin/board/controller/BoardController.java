package com.fupto.back.admin.board.controller;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.BoardSearchDto;
import com.fupto.back.admin.board.service.BoardService;
import com.fupto.back.entity.Board;
import com.fupto.back.repository.BoardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("admin/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private BoardService boardService;

    public BoardController(BoardService boardService, BoardRepository boardRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

    @GetMapping
    public List<Board> getList() {return  boardRepository.findAll();}
    // 목록 - 페이징
    @GetMapping("/list")
    public ResponseEntity<BoardResponseDto> getSearchList(@ModelAttribute BoardSearchDto searchDto){
        return ResponseEntity.ok(boardService.getList(searchDto));
    }
}




