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
    private BoardService boardService;
    private BoardRepository boardRepository;

    public BoardController(BoardService boardService, BoardRepository boardRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

    @GetMapping("all")
    public List<Board> getBoards() {
        return boardRepository.findAll();
    }

    @GetMapping("/list")
    public List<BoardListDto> getList() { return boardService.getList();}

    @GetMapping("{id}")
    public BoardListDto getBoardById(@PathVariable Long id) { return boardService.getBoardById(id);}

    @GetMapping("/search")
    public ResponseEntity<BoardResponseDto> getSearch(
            @ModelAttribute BoardSearchDto boardSearchDto
    ){
        return ResponseEntity.ok(boardService.getSearch(boardSearchDto));
    }


}



