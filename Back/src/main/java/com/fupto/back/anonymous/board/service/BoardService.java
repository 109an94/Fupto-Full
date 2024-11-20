package com.fupto.back.anonymous.board.service;

import com.fupto.back.anonymous.board.dto.NoticeDefaultDto;
import com.fupto.back.anonymous.board.dto.NoticeListDto;
import com.fupto.back.anonymous.board.dto.NoticeSearchDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BoardService {

    // notice 조회
    List<NoticeListDto> getNotice();

//    NoticeDefaultDto getSearch(NoticeSearchDto noticeSearchDto);
}
