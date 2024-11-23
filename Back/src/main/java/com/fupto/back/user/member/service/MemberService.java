package com.fupto.back.user.member.service;

import com.fupto.back.entity.Favorite;
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

    void addEmitter(Long id, SseEmitter emitter);
    void removeEmitter(Long id);
    void sendAlert(Long memberId, String message);

    void updateAlertPrice(Long memberID, Long favoriteId, Integer alertPrice);
//    void favoriteChecker(Long productId, Integer newPrice);
}
