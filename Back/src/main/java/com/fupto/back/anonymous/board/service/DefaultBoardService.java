package com.fupto.back.anonymous.board.service;

import com.fupto.back.anonymous.board.dto.NoticeDefaultDto;
import com.fupto.back.anonymous.board.dto.NoticeListDto;
import com.fupto.back.anonymous.board.dto.NoticeSearchDto;
import com.fupto.back.entity.Board;
import com.fupto.back.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultBoardService implements BoardService{

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    public DefaultBoardService(BoardRepository boardRepository,
                               ModelMapper modelMapper) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<NoticeListDto> getNotice() {
        List<Board> boards = boardRepository.findAll();
        List<NoticeListDto> noticeListDtos = boards.stream()
                .map(board -> {
                    NoticeListDto noticeListDto = modelMapper.map(board, NoticeListDto.class);
                    return noticeListDto;
                })
                .toList();

        return noticeListDtos;
    }


//    @Override
//    public NoticeDefaultDto getSearch(NoticeSearchDto noticeSearchDto) {
//        return null;
//    }


}
