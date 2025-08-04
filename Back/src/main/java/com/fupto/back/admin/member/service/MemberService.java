package com.fupto.back.admin.member.service;

import com.fupto.back.admin.member.dto.*;
import com.fupto.back.entity.Favorite;
import com.fupto.back.entity.Member;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;


public interface MemberService {
    List<Member> getList();
    MemberResponseDto getMemberList(Integer page,
                                    Integer size,
                                    String memberType,
                                    String gender,
                                    String searchType,
                                    String searchKeyWord,
                                    String dateType,
                                    String startDate,
                                    String endDate);
    MemberResponseDto getMemberList(MemberSearchDto memberSearchDto);
//    MemberListDto getMemberById(Long id);
    List<MemberListDto> getMemberWithDetails();
    MemberDetailDto getMemberById (Long id);

    FavoriteListDto getFavorite (Favorite favorite);
    FavoriteResponseDto getFavCount (Long memberId);

    Resource getProductImage(Long id) throws IOException;
}
