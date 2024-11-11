package com.fupto.back.admin.member.service;

import com.fupto.back.admin.member.dto.MemberListDto;
import com.fupto.back.admin.member.dto.MemberResponseDto;
import com.fupto.back.admin.member.dto.MemberSearchDto;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultMemberService implements MemberService{

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public DefaultMemberService(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Member> getList() {
        return memberRepository.findAll();
    }

    @Override
    public MemberResponseDto getMemberList(MemberSearchDto SearchDto) {
        return getMemberList(SearchDto.getPage()
                ,SearchDto.getSize()
                ,SearchDto.getMemberType()
                ,SearchDto.getGender()
                ,SearchDto.getSearchType()
                ,SearchDto.getSearchKeyWord());
    }

    @Override
    public MemberResponseDto getMemberList(Integer page,
                                           Integer size,
                                           String memberType,
                                           String gender,
                                           String searchType,
                                           String searchKeyWord) {

        //페이지 처리
        Pageable pageable = PageRequest.of(page - 1,
                size,
                Sort.by(searchType).descending());

        // userId와 nickname 설정
        String userId = searchType.equals("userId") ? searchKeyWord : null;
        String nickname = searchType.equals("nickname") ? searchKeyWord : null;
        String email = searchType.equals("email") ? searchKeyWord : null;
            //genderf 는 String 타입으로안해도 되는 데 가독성을 위해 f로 변환
        String genderf = (gender == null || gender.isEmpty()) ? null : gender;
        String memberTypef = (memberType == null || memberType.isEmpty()) ? null : memberType;
        // 레포지토리 메소드 호출
        Page<Member> memberPage = memberRepository.searchMember(memberTypef, genderf, userId, nickname, email, pageable);

        List<MemberListDto> memberListDtos = memberPage
                .getContent()
                .stream()
                .map(member -> modelMapper.map(member, MemberListDto.class))
                .toList();

        long totalCount = memberPage.getTotalElements();
        long totalPages = memberPage.getTotalPages();


        // 페이징 네이션 번호 리스트 생성
        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        };

        return MemberResponseDto.builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .hasNextPage(memberPage.hasNext())
                .hasPrevPage(memberPage.hasPrevious())
                .pages(pages)
                .members(memberListDtos)
                .build();
    }

    @Override
    public MemberListDto getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElse(null);
        MemberListDto memberListDto = modelMapper.map(member, MemberListDto.class);

        return memberListDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberListDto> getMemberWithDetails() {
        List<Member> members = memberRepository.findAll();
        System.out.println(members);
        List<MemberListDto> memberListDto = members.stream()
                .map(a -> {
                    System.out.println("member = " + a);
                    MemberListDto dto = modelMapper.map(a, MemberListDto.class);
                    System.out.println("dto = " + dto);
                    System.out.println("id: " + dto.getId());
                        return dto;
                })
                .toList();
        System.out.println(memberListDto);

        return memberListDto;
    }
}
