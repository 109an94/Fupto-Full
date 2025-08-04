package com.fupto.back.user.member.service;

import com.fupto.back.entity.*;
import com.fupto.back.repository.*;
import com.fupto.back.user.emitter.service.EmitterService;
import com.fupto.back.user.member.dto.BoardListDto;
import com.fupto.back.user.member.dto.FavoriteListDto;
import com.fupto.back.user.member.dto.MemberEditDto;
import com.fupto.back.user.member.dto.MemberResponseDto;
import com.fupto.back.user.member.exception.InvalidPasswordException;
import com.fupto.back.user.member.exception.PasswordMismatchException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service("userMemberService")
@Transactional
public class DefaultMemberService implements MemberService {


    private final BoardRepository boardRepository;
    @Value("uploads")
    private String uploadPath;

    private final PriceHistoryRepository priceHistoryRepository;
    private final EmitterService emitterService;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductImageRepository productImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public DefaultMemberService(MemberRepository memberRepository,
                                ModelMapper modelMapper,
                                PasswordEncoder passwordEncoder,
                                ProductImageRepository productImageRepository,
                                FavoriteRepository favoriteRepository,
                                ProductRepository productRepository,
                                EmitterService emitterService,
                                PriceHistoryRepository priceHistoryRepository, BoardRepository boardRepository) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.productImageRepository = productImageRepository;
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.emitterService = emitterService;
        this.priceHistoryRepository = priceHistoryRepository;
        this.boardRepository = boardRepository;
    }


    @Transactional
    public MemberResponseDto editMember (String userId, MemberEditDto dto){

        if (!memberRepository.existsByUserId(userId)){
            throw new EntityNotFoundException("회원이 존재하지 않습니다.");
        }

        Member member = memberRepository.findOptionalByUserId(userId).orElseThrow(()->
                new EntityNotFoundException(userId+"가 존재하지 않습니다."));
        if (!passwordEncoder.matches(dto.getPassword(),member.getPassword())){
            throw new InvalidPasswordException("입력하신 비밀번호가 일치하지 않습니다.");
        }

        if (StringUtils.hasText(dto.getNewPassword())){
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())){
                throw new PasswordMismatchException("새 비밀번호가 일치하지 않습니다.");
            }
            member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        if (StringUtils.hasText(dto.getNickname())){
        member.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            member.setEmail(dto.getEmail());
        }
//        if (dto.getBirthDate() == null){
        if (StringUtils.hasText(dto.getBirthDate())) {
            try{
                LocalDate localDate = LocalDate.parse(dto.getBirthDate());
                Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(32400);
            member.setBirthDate(instant);
            } catch (Exception e){
                try {
                    Instant instant = Instant.parse(dto.getBirthDate()).plusSeconds(32400);
                    member.setBirthDate(instant);
                } catch (Exception e2) {
                    throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
                }
            }
        }
//            member.setUpdateDate(Instant.now().plusSeconds(32400));

        member.setUpdateDate(dto.getUpdateDate());
        Member savedMember = memberRepository.save(member);
        return modelMapper.map(savedMember, MemberResponseDto.class);
    }

    @Override
    public MemberResponseDto getMember(Long id) {
        if (memberRepository.findById(id) == null)
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");

        Optional<Member> member = memberRepository.findById(id);

        return modelMapper.map(member, MemberResponseDto.class);
    }

    //--------------favorite 관련 매서드-------------------------
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
    @Override
    public List<FavoriteListDto> getFavorites(Long id) {
        List<Favorite> favorites = favoriteRepository.findAllByMemberIdAndStateIsTrue(id);
        if (favorites == null || favorites.isEmpty()){
            return Collections.emptyList();
        }
        List<FavoriteListDto> dtoList = new ArrayList<>();
        for (Favorite favorite : favorites){
            Integer priceHistories = priceHistoryRepository.findLowestCurrentPrice(favorite.getMappingId());
            Product product = productRepository.findById(favorite.getMappingId()).orElse(null);
            if (product == null){
                continue;
            }
            Brand brand = product.getBrand();

            FavoriteListDto dto = new FavoriteListDto();
            dto.setId(favorite.getId());
            dto.setProductId(product.getId());
            dto.setProductName(product.getProductName());
            dto.setProductPrice(priceHistories);
            dto.setMemberId(favorite.getMember().getId());
            dto.setMemberName(favorite.getMember().getNickname());
            dto.setCreateDate(favorite.getCreateDate());
            dto.setProductBrandName(brand.getEngName());
            dto.setAlertPrice(favorite.getAlertPrice());

            dtoList.add(dto);
        }
        return dtoList;
    }

    //------board 관련 매서드
    @Override
    public List<BoardListDto> getBoards(Long memberId) {
        List<Board> boards = boardRepository.findAllByRegMemberIdAndActiveIsTrue(memberId);


        return boards.stream().map(this::convertToBoardListDto)
                .collect(Collectors.toList());
    }

    @Override
    public Resource getBoardImage(Long id) throws IOException {
        Optional<Board> board = boardRepository.findById(id);
        String boardImg = board.map(Board::getImg).orElse("defaultImgPath");
        Path imagePath = Paths.get(boardImg);
        Resource resource = new FileSystemResource(imagePath.toFile());
        if (!resource.exists()){
            throw new FileNotFoundException("이미지 파일을 찾을 수 없습니다.");
        }
        return resource;
    }

    private BoardListDto convertToBoardListDto(Board board){
        return BoardListDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .img(board.getImg())
                .regMemberId(board.getRegMember().getId())
                .regMemberNickName(board.getRegMember().getNickname())
                .createdAt(board.getCreatedAt())
                .active(board.getActive())
                .build();
    }



    //--------------alert 영역--------------------
    @Override
    public String createAlertMessage(Long mappingId, Integer newPrice, Integer alertPrice) {
        if (alertPrice != null) {
            return String.format("관심 상품(ID: %d)의 가격이 설정하신 %d원 이하로 떨어져 %d원으로 변경되었습니다.", mappingId, alertPrice, newPrice);
        } else {
            return String.format("관심 상품(ID: %d)의 가격이 %d원으로 변경되었습니다.", mappingId, newPrice);
    }
        }

    @Transactional
    @Override
    public void updateAlertPrice(Long memberId, Long mappingId, Integer alertPrice) {

        Favorite favorite = favoriteRepository.findByMemberIdAndMappingId(memberId, mappingId)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));

        Integer oldAlertPrice = favorite.getAlertPrice();
        favorite.setAlertPrice(alertPrice);
        favoriteRepository.save(favorite);

        // 새로 설정한 알림 가격에 대해 즉시 체크
        checkAlertCondition(favorite, oldAlertPrice);
    }

    @Override
    public void checkAlertCondition(Favorite favorite, Integer oldAlertPrice){
        Integer lowestPrice = findLowestPriceByMappingId(favorite.getMappingId());
//        System.out.println("checkAlertCondition lowestPrice : "+ lowestPrice);

        if ((oldAlertPrice == null && favorite.getAlertPrice() != null)
                || (favorite.getAlertPrice() != null && lowestPrice <= favorite.getAlertPrice())){
            String message = createAlertMessage(favorite.getMappingId(), lowestPrice, favorite.getAlertPrice());
            System.out.println("checkAlertCondition message : "+message);
        }
    }

    @Transactional
    @Override
    public void checkerforfavPrice(Long productId, Integer newPrice){
        List<Favorite> favorites = favoriteRepository.findByMappingId(productId);
        Integer lowestPrice = findLowestPriceByMappingId(productId);
        String alertType = "PRICE_ALERT";
        for (Favorite favorite : favorites){
            Integer alertPrice = favorite.getAlertPrice();
            if (alertPrice == null || lowestPrice <= alertPrice){
                String message = createAlertMessage(productId, newPrice, alertPrice);
                emitterService.sendToEmitter(favorite.getMember().getId(), alertType, message);
            }
        }
    }

    //최저가 찾는 로직
    @Override
    public Integer findLowestPriceByMappingId(Long mappingId) {
        List<Product> products = productRepository.findAllByMappingId(mappingId);
        //mappingid가 변경 된 경우 찾아가는 로직
        if (products == null || products.isEmpty()){
            Product findProduct = productRepository.findById(mappingId).orElse(null);
            Long findProductId = findProduct.getMappingId().longValue();
            products = productRepository.findAllByMappingId(findProductId);
        }
        List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());

        return priceHistoryRepository.findLowestPriceByProductIdsAndLatestDate(productIds)
                .orElseThrow(() -> new EntityNotFoundException("No price found for mapping id: " + mappingId));
    }



}
