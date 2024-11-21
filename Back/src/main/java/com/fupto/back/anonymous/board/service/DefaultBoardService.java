package com.fupto.back.anonymous.board.service;

import com.fupto.back.anonymous.board.dto.BoardDefaultDto;
import com.fupto.back.anonymous.board.dto.BoardListDto;
import com.fupto.back.anonymous.board.dto.BoardSearchDto;

import com.fupto.back.entity.Board;

import com.fupto.back.repository.BoardCategoryRepository;
import com.fupto.back.repository.BoardRepository;
import com.fupto.back.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DefaultBoardService implements BoardService{

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final BoardCategoryRepository boardCategoryRepository;

    public DefaultBoardService(BoardRepository boardRepository,
                               BoardCategoryRepository boardCategoryRepository,
                               ModelMapper modelMapper,
                               MemberRepository memberRepository) {

        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
        this.boardCategoryRepository = boardCategoryRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<BoardListDto> getAllList() {
        List<Board> boards = boardRepository.findAll();
        List<BoardListDto> boardListDtos = boards.stream()
                .map(board -> {
                    BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;
                })
                .toList();
        return boardListDtos;
    }

    @Override
    public BoardDefaultDto getSearch(BoardSearchDto boardSearchDto) {
        Sort sort = Sort.by(
                boardSearchDto.getSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                boardSearchDto.getSortBy() != null ? boardSearchDto.getSortBy() : "createDate"
        );
        Pageable pageable = PageRequest.of(boardSearchDto.getPage() - 1, boardSearchDto.getSize(), sort);

        Page<Board> boardPage = boardRepository.searchBoards(
                boardSearchDto.getSearchKeyWord(),
                boardSearchDto.getSearchType(),
                boardSearchDto.getBoardCategory(),
                boardSearchDto.getActive(),
                pageable
        );

        List<BoardListDto> boardListDtos = boardPage
                .getContent()
                .stream()
                .map(board -> {
                    BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;
                })
                .toList();

        long totalElements = boardPage.getTotalElements();
        long totalPages = boardPage.getTotalPages();

        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        return BoardDefaultDto
                .builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNextPage(boardPage.hasNext())
                .hasPreviousPage(boardPage.hasPrevious())
                .pages(pages)
                .boards(boardListDtos)
                .build();
    }
}
