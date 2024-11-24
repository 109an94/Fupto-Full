package com.fupto.back.admin.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fupto.back.admin.board.dto.*;
import com.fupto.back.admin.board.service.BoardService;
import com.fupto.back.admin.brand.dto.BrandCreateDto;
import com.fupto.back.admin.brand.dto.BrandListDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController("adminBoardController")
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
    public ResponseEntity<BoardListDto> createPost(
            @RequestParam("boardData") String boardDataJson,  // JSON 데이터로 게시글 정보 받음
            @RequestParam(value = "file", required = false) MultipartFile file) {  // 파일 (선택적)

        try {
            // boardDataJson을 BoardListDto 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            BoardListDto boardListDto = objectMapper.readValue(boardDataJson, BoardListDto.class);

            // 서비스 호출하여 게시글 생성 (파일도 있을 수 있음)
            BoardListDto createdBoard = boardService.createPost(boardListDto, file);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

//    // 게시글 등록
//    @PostMapping("/post")
//    public ResponseEntity<BoardListDto> createPost( @RequestBody BoardListDto boardListDto){
//
//        BoardListDto createBoard = boardService.createPost(boardListDto);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(createBoard);
//    }

    // 선택한 게시글 수정
    @GetMapping("{id}/edit")
    public ResponseEntity<BoardListDto> show(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.show(id));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardListDto> updateBoard(
            @PathVariable Long id,
            @RequestPart("boardData") String boardDtaJson,
            @RequestPart(value = "file",required = false) MultipartFile file) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            BoardUpdateDto boardUpdateDto = objectMapper.readValue(boardDtaJson, BoardUpdateDto.class);

            BoardListDto updateBoard = boardService.update(id,boardUpdateDto, file);
            return ResponseEntity.ok(updateBoard);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }




//    @PutMapping("/{id}")
//    public BoardResponseDto updatePost(@PathVariable Long id, @RequestBody BoardRequestsDto requestsDto) throws Exception {
//        return boardService.updatePost(id, requestsDto);
//    }



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



