package com.fupto.back.anonymous.board.controller;


import com.fupto.back.anonymous.board.dto.NoticeDefaultDto;
import com.fupto.back.anonymous.board.dto.NoticeListDto;
import com.fupto.back.anonymous.board.dto.NoticeSearchDto;
import com.fupto.back.anonymous.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/notice")
    public List<NoticeListDto> getNotice() {
        return boardService.getNotice();
    }

//    @GetMapping
//    public ResponseEntity<NoticeDefaultDto> searchBoard(
//            @ModelAttribute NoticeSearchDto noticeSearchDto
//    ){
//        return ResponseEntity.ok(boardService.getSearch(noticeSearchDto));
//    }
}
