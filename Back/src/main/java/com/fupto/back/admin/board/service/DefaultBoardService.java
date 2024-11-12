package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.admin.board.dto.BoardResponseDto;
import com.fupto.back.admin.board.dto.BoardSearchDto;
import com.fupto.back.entity.Board;
import com.fupto.back.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultBoardService implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    public DefaultBoardService(BoardRepository boardRepository) {

        this.boardRepository = boardRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<Board> getBoards() {
        return boardRepository.findAll();
    }

    @Override
    public List<BoardListDto> getList() {
//        List<Board> boards = boardRepository.findAll();
        List<BoardListDto> boardListDtos = getBoards()
                .stream()
                .map(board -> {
                    BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;
                })
                .toList();

        return boardListDtos;
    }

    @Override
    public BoardListDto getBoardById(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);

        return boardListDto;
    }

    @Override
    public BoardResponseDto getSearch(BoardSearchDto boardSearchDto) {

        Pageable pageable = PageRequest.of(boardSearchDto.getPage()-1,
                boardSearchDto.getSize(),
                Sort.by(boardSearchDto.getSort()).descending());

        Page<Board> boards;
        if (){}


        List<BoardListDto> boardListDtos = boards
                .getContent()
                .stream()
                .map(board -> {BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);})
                .toList();

        return BoardResponseDto.builder()
                .boards(boardListDtos)
                .build();
    }


}
