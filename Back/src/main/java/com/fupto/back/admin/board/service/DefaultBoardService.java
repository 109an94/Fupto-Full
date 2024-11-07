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

import java.util.List;
import java.util.stream.LongStream;

@Service("adminBoardService")
public class DefaultBoardService implements BoardService {

    private BoardRepository boardRepository;
    private ModelMapper modelMapper;

    public DefaultBoardService(BoardRepository boardRepository, ModelMapper modelMapper) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BoardResponseDto getList(BoardSearchDto searchDto) {
        return getList(searchDto.getPage(), searchDto.getKeyword(), searchDto.getCategoryIds());
    }

    @Override
    public BoardResponseDto getList(Integer page, String korName, List<Long> categoryIds) {
        Sort sort = Sort.by("createDate").descending();
        Pageable pageable = PageRequest.of(page-1,6,sort);
        Page<Board> boardPage = (Page<Board>) boardRepository.findAll(pageable);

        List<BoardListDto> boardListDtos = boardPage
                .getContent()
                .stream()
                .map(board -> {
                    BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);

                    return boardListDto;
                })
                .toList();

        long totalCount = boardPage.getTotalElements();
        long totalPages = boardPage.getTotalPages();
        boolean nextPage = boardPage.hasNext();
        boolean prevPage = boardPage.hasPrevious();

        page = (page == null) ? 1 : page;
        int offset = (page - 1) % 5;
        int startNum = page - offset;
        List<Long> pages= LongStream
                .range(startNum, startNum + 5)
                .boxed().toList();

        return BoardResponseDto.builder()
                .boards(boardListDtos)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .nextPage(nextPage)
                .prevPage(prevPage)
                .pages(pages)
                .build();
    }


    // 등록
//    @Override
//    public BoardListDto create(BoardListDto boardListDto) {
//        Board board = boardRepository.save(BoardMapper.mapToEntity(boardListDto));
//
//        BoardListDto boardone = BoardMapper.mapToDto(boardRepository.findById(board.getId()).get());
//        return BoardMapper.mapToDto(board);
//    }

    // 삭제
//    @Override
//    public void delete(Long id) {
//        boardRepository.deleteById(id);
//    }


    // 목록


//    @Override
//    public List<Board> getList() {return boardRepository.findAll();}
//
//    @Override
//    public List<BoardListDto> findAllBoards() {
//        List<Board> boards = boardRepository.findAll();
//        List<BoardListDto> boardListDtos = boards.stream()
//                .map(board -> modelMapper.map(board, BoardListDto.class))
//                .toList();
//        return boardListDtos;
//    }
//    @Override
//    public BoardResponseDto getList(int page) {
//        Sort sort = Sort.by("createDate").descending();
//        Pageable pageable = PageRequest.of(page-1, 10, sort);
//
//        Long totalCount = boardRepository.count();
//
//        Page<Board> boards = boardRepository.findAll(pageable);
//
//        List<BoardListDto> boardListDtos = board
//        return null;
//    }

}
