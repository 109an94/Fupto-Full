package com.fupto.back.anonymous.board.service;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.anonymous.board.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardService {

    List<BoardDto> findAll();

    DefaultDto userSearch(SearchDto searchDto);

    DetailDto getById(Long id);

    BoardDto userInActive(Long id, Boolean active);

    BoardDto userPost(BoardDto boardDto, MultipartFile file) throws IOException;

    BoardDto show(Long id);

    BoardDto update(Long id, UserUpdateDto userUpdateDto, MultipartFile file) throws IOException;
}
