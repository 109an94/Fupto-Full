package com.fupto.back.anonymous.board.service;

import com.fupto.back.anonymous.board.dto.BoardDto;
import com.fupto.back.anonymous.board.dto.DefaultDto;
import com.fupto.back.anonymous.board.dto.DetailDto;
import com.fupto.back.anonymous.board.dto.SearchDto;
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

    private BoardRepository boardRepository;
    private ModelMapper modelMapper;
    private BoardCategoryRepository boardCategoryRepository;
    private MemberRepository memberRepository;

    public DefaultBoardService(BoardRepository boardRepository,
                               ModelMapper modelMapper,
                               BoardCategoryRepository boardCategoryRepository,
                               MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
        this.boardCategoryRepository = boardCategoryRepository;
        this.memberRepository = memberRepository;
    }
    @Override
    public List<BoardDto> findAll() {
        List<Board> boards = boardRepository.findAll();
        List<BoardDto> boardDtos = boards.stream()
                .filter(board -> board.getActive() != null && board.getActive())
                .map(board -> {
                    BoardDto boardDto = modelMapper.map(board, BoardDto.class);
                    return boardDto;
                })
                .toList();
        return boardDtos;
    }

    @Override
    public DefaultDto userSearch(SearchDto searchDto) {
        Sort sort = Sort.by(
                searchDto.getSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                searchDto.getSortBy() != null ? searchDto.getSortBy() : "createdAt"
        );
        Pageable pageable = PageRequest.of(searchDto.getPage()-1, searchDto.getSize(), sort);

        Page<Board> boards = boardRepository.userSearchBoards(
                searchDto.getSearchKeyWord(),
                searchDto.getSearchType(),
                searchDto.getBoardCategory(),
                pageable
        );

        List<BoardDto> boardDtos = boards
                .getContent()
                .stream()
                .filter(board -> board.getActive() != null && board.getActive())
                .map(board -> {BoardDto boardDto = modelMapper.map(board, BoardDto.class);
                return boardDto;
                })
                .toList();

        long totalElements = boards.getTotalElements();
        long totalPages = boards.getTotalPages();

        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        return DefaultDto
                .builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNextPage(boards.hasNext())
                .hasPreviousPage(boards.hasPrevious())
                .pages(pages)
                .boards(boardDtos)
                .build();
    }

    @Override
    public DetailDto getById(Long id) {
        Board board = boardRepository.findById(id).orElse(null);

        return DetailDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .boardCategoryName(board.getBoardCategory().getName())
                .regMemberNickName(board.getRegMember().getNickname())
                .build();
    }


}
