package com.fupto.back.admin.board.controller;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.admin.board.service.BoardService;
import com.fupto.back.entity.Board;
import com.fupto.back.entity.BoardCategory;
import com.fupto.back.repository.BoardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("admin/boards")
public class BoardController {

    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/list")
    public ResponseEntity<BoardResponseDto> getList(
            @ModelAttribute BoardSearchDto boardSearchDto) {
        System.out.println(boardSearchDto.toString());
        return ResponseEntity.ok(boardService.getList(boardSearchDto));
    }
}



