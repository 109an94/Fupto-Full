package com.fupto.back.anonymous.board.controller;

import com.fupto.back.anonymous.board.dto.BoardDefaultDto;
import com.fupto.back.anonymous.board.dto.BoardListDto;
import com.fupto.back.anonymous.board.dto.BoardSearchDto;

import com.fupto.back.anonymous.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/all")
    public List<BoardListDto> getAllList() {
        return boardService.getAllList();
    }

    @GetMapping
    public ResponseEntity<BoardDefaultDto> searchBoard(
            @ModelAttribute BoardSearchDto boardSearchDto
    ) {
        return ResponseEntity.ok(boardService.getSearch(boardSearchDto));
    }




}
