package com.fupto.back.user.member.service;

import com.fupto.back.entity.Favorite;
import com.fupto.back.user.member.dto.BoardListDto;
import com.fupto.back.user.member.dto.FavoriteListDto;
import com.fupto.back.user.member.dto.MemberEditDto;
import com.fupto.back.user.member.dto.MemberResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface MemberService {
    MemberResponseDto editMember (String userId, MemberEditDto signupDto);
    MemberResponseDto getMember (Long id);
    Resource getProductImage(Long id) throws IOException;
    List<FavoriteListDto> getFavorites (Long id);
    List<BoardListDto> getBoards (Long memberId);
    Resource getBoardImage(Long id) throws IOException;

    void updateAlertPrice( Long favoriteId, Long memberID, Integer alertPrice);

    //알림 전송 및 알림 생성

    String createAlertMessage(Long mappingId, Integer newPrice, Integer alertPrice);
    void checkAlertCondition(Favorite favorite, Integer oldAlertPrice);
//    private boolean isPriceDropped(Favorite favorite) 가격떨어졌는 지 현재와 alert비교
    Integer findLowestPriceByMappingId(Long mappingId);


    void checkerforfavPrice(Long productId, Integer newPrice);
}
