package com.fupto.back.anonymous.board.controller;

import com.fupto.back.anonymous.board.dto.BoardDto;
import com.fupto.back.anonymous.board.dto.DefaultDto;
import com.fupto.back.anonymous.board.dto.DetailDto;
import com.fupto.back.anonymous.board.dto.SearchDto;
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
    public List<BoardDto> findAll(){
        return boardService.findAll();
    }

    @GetMapping("list")
    public ResponseEntity<DefaultDto> userSearchBoard(
            @ModelAttribute SearchDto searchDto
    ){
        return ResponseEntity.ok(boardService.userSearch(searchDto));
    }

    @GetMapping("{id}/detail")
    public ResponseEntity<DetailDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(boardService.getById(id));
    }

    @PatchMapping("{id}/active")
    public ResponseEntity<BoardDto> userDelete(
            @PathVariable Long id,
            @RequestParam Boolean active) throws Exception
    {
        return ResponseEntity.ok(boardService.userDelete(id, active));
    }

}
