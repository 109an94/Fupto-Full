package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.admin.board.service.BoardService;
import com.fupto.back.entity.Board;
import com.fupto.back.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class DefaultBoardService implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    public DefaultBoardService(BoardRepository boardRepository, ModelMapper modelMapper) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
    }

//    public DefaultBoardService(BoardRepository boardRepository, ModelMapper modelMapper) {
//        this.boardRepository = boardRepository;
//        this.modelMapper = modelMapper;
//    }

// ========== 전체 조회 =========================================================================
    @Override
    public List<BoardListDto> getList() {
        List<Board> boards = boardRepository.findAll();
        List<BoardListDto> boardListDtos = boards.stream()
                .map(board -> {BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;})
                .toList();
        return boardListDtos;
    }
//    @Override
//    public List<BoardResponseDto> getList() {
//        return boardRepository.findAllByOrderByModifiedAtDesc().stream().map(BoardResponseDto::new).toList();
//    }




// ========== 등록 =========================================================================
    @Override
    public BoardResponseDto createPost(BoardRequestsDto requestsDto) {
        return null;
    }




//    @Override
//    public BoardResponseDto createPost(BoardRequestsDto requestsDto) {
//        Board board = new Board();
//        board.setTitle(requestsDto.getTitle());
//        board.setContents(requestsDto.getContents());
//        board.setActive(requestsDto.getActive());  // author 필드에 값 설정
//        board.setPassword(requestsDto.getPassword());
//        boardRepository.save(board);
//        return new BoardResponseDto(board);
//    }

// ========== id 조회 =========================================================================
    @Override
    public BoardDetailDto getBoardById(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board == null) {
            return null;
        }
        BoardDetailDto boardDetailDto = modelMapper.map(board, BoardDetailDto.class);

        return boardDetailDto;
    }


//    @Override
//    public BoardResponseDto getPost(Long id) {
//        return boardRepository.findById(id).map(BoardResponseDto::new)
//                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
//    }

// ========== 수정 =========================================================================
//    @Override
//    public BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception {
//        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
//        if (!requestsDto.getPassword().equals(board.getPassword()))
//            throw new Exception("비밀번호가 일치하지 않습니다.");
//
//        board.update(requestsDto);
//        boardRepository.save(board);
//
//        return new BoardResponseDto(board);
//    }

    // 게시글 삭제
    @Override
    public SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        if (!requestsDto.getPassword().equals(board.getPassword()))
            throw new Exception("비밀번호가 일치하지 않습니다.");

        boardRepository.deleteById(id);
        return new SuccessResponseDto(true);
    }

}


