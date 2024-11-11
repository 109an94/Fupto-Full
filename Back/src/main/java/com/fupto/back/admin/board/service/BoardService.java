package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.entity.Board;

import java.util.List;


public interface BoardService {
    BoardResponseDto getList(BoardSearchDto boardSearchDto);
}
