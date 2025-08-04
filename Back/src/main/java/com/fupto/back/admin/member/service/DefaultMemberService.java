package com.fupto.back.admin.member.service;

import com.fupto.back.admin.member.dto.*;
import com.fupto.back.entity.*;
import com.fupto.back.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("adminMemberService")
public class DefaultMemberService implements MemberService{

    private final ProductRepository productRepository;
    @Value("uploads")
    private String uploadPath;

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductImageRepository productImageRepository;

    public DefaultMemberService(MemberRepository memberRepository, ModelMapper modelMapper, BoardRepository boardRepository, FavoriteRepository favoriteRepository, ProductImageRepository productImageRepository, ProductRepository productRepository) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.boardRepository = boardRepository;
        this.favoriteRepository = favoriteRepository;
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
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
                ,SearchDto.getSearchKeyWord()
                ,SearchDto.getDateType()
                ,SearchDto.getStartDate()
                ,SearchDto.getEndDate());
    }

    @Override
    public MemberResponseDto getMemberList(Integer page,
                                           Integer size,
                                           String memberType,
                                           String gender,
                                           String searchType,
                                           String searchKeyWord,
                                           String dateType,
                                           String startDate,
                                           String endDate) {

        //페이지 처리
        Pageable pageable = PageRequest.of(page - 1,
                size,
                Sort.by(searchType).descending());

        // userId와 nickname 설정 searchType = null  / userid nickname email
        String userId = searchType.equals("userId") ? searchKeyWord : null;
        String nickname = searchType.equals("nickname") ? searchKeyWord : null;
        String email = searchType.equals("email") ? searchKeyWord : null;

            //genderf 는 String 타입으로안해도 되는 데 가독성을 위해 f로 변환
        String genderf = (gender == null || gender.isEmpty()) ? null : gender;
        String memberTypef =(memberType == null || memberType.isEmpty()) ? null : memberType;

        // 레포지토리 메소드 호출
        String dateTypef = (dateType != null && !dateType.isEmpty()) ? dateType :"regDate";
        Instant startDateI = (startDate == null)?null
                :LocalDate.parse(startDate)
                .atStartOfDay(ZoneId.of("Asia/Seoul"))
                .toInstant();
        Instant endDateI = (endDate == null)?null
                :LocalDate.parse(endDate)
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();
        Page<Member> memberPage = memberRepository.searchMember(memberTypef, genderf, userId, nickname, email, dateTypef,startDateI,endDateI ,pageable);

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
        }

        return MemberResponseDto.builder()
                .totalCount(totalCount)
                .totalPages(totalPages)
                .hasNextPage(memberPage.hasNext())
                .hasPrevPage(memberPage.hasPrevious())
                .pages(pages)
                .members(memberListDtos)
                .build();
    }


    @Override//이제 안씀 따로 받아오는 게 목표
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
    @Override
    public MemberDetailDto getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null){
            return null;
        }

        MemberDetailDto memberDetail = modelMapper.map(member, MemberDetailDto.class);
        List<Board> boards = boardRepository.findByRegMemberId(id);
        List<Favorite> favorites = favoriteRepository.findAllByMemberId(id);
        System.out.println(boards);
        System.out.println(favorites);

        List<BoardListDto> boardListDtos = boards.stream()
                .map(this::getBoard)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        memberDetail.setBoardCount(boardListDtos.size());
        memberDetail.setBoardList(boardListDtos);
        System.out.println(boardListDtos.size());

        List<FavoriteListDto> favoriteListDtos = favorites.stream()
                .map(this::getFavorite)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        memberDetail.setFavoriteCount(favoriteListDtos.size());
        memberDetail.setFavoriteList(favoriteListDtos);

        return memberDetail;
    }

    private BoardListDto getBoard (Board board){
        if (board == null){
            return null;
        }
        BoardListDto dto = new BoardListDto();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
//        dto.setDate(board.getDate());

        return dto;
    }

    @Override
    public FavoriteListDto getFavorite (Favorite favorite){
        if (favorite == null){
            return null;
        }
        Product product = productRepository.findById(favorite.getMappingId()).orElse(null);

//        Long prductId = favorite.getProduct().getId();
        Brand brand = product.getBrand();

        FavoriteListDto dto = new FavoriteListDto();
        dto.setId(favorite.getId());
        dto.setProductId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setMemberId(favorite.getMember().getId());
        dto.setMemberName(favorite.getMember().getNickname());
        dto.setCreateDate(favorite.getCreateDate());
        dto.setProductBrandName(brand.getEngName());

        return dto;
    }
    @Override // 이거 생각해보니 안씀
    public FavoriteResponseDto getFavCount (Long memberId){
            List<Favorite> favorites = favoriteRepository.findAllByMemberId(memberId);
            Long totalCount = (long) favorites.size();

            List<FavoriteListDto> favoriteList = favorites.stream()
                    .map(this::getFavorite)
                    .collect(Collectors.toList());

        return FavoriteResponseDto.builder()
                .totalCount(totalCount)
                .favoriteList(favoriteList)
                .build();
    }

    @Override
    public Resource getProductImage(Long id) throws IOException {
        Integer order = 1;
        ProductImage productImage = productImageRepository.findByProductIdAndDisplayOrder(id, order)
                .orElseThrow( () -> new EntityNotFoundException("해당 순서의 이미지를 찾을 수 없습니다."));
        Path imagePath = Paths.get(uploadPath, productImage.getFilePath());
        Resource resource = new FileSystemResource(imagePath.toFile());

        if (!resource.exists()){
            throw new FileNotFoundException("이미지 파일을 찾을 수 없습니다.");
        }


        return resource;
    }

}
