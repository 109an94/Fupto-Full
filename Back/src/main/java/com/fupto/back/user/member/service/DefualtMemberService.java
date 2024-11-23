package com.fupto.back.user.member.service;

import com.fupto.back.entity.*;
import com.fupto.back.repository.FavoriteRepository;
import com.fupto.back.repository.MemberRepository;
import com.fupto.back.repository.ProductImageRepository;
import com.fupto.back.repository.ProductRepository;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("userMemberService")
@Transactional
public class DefualtMemberService implements MemberService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Value("uploads")
    private String uploadPath;

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductImageRepository productImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public DefualtMemberService(MemberRepository memberRepository,
                                ModelMapper modelMapper,
                                PasswordEncoder passwordEncoder,
                                ProductImageRepository productImageRepository,
                                FavoriteRepository favoriteRepository,
                                ProductRepository productRepository) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.productImageRepository = productImageRepository;
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
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
        System.out.println(member);

        return modelMapper.map(member, MemberResponseDto.class);
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

    @Override
    public List<FavoriteListDto> getFavorites(Long id) {
        List<Favorite> favorites = favoriteRepository.findAllByMemberId(id);
        if (favorites == null || favorites.isEmpty()){
            return Collections.emptyList();
        }
        List<FavoriteListDto> dtoList = new ArrayList<>();
        for (Favorite favorite : favorites){
            Product product = productRepository.findById(favorite.getMappingId()).orElse(null);
            if (product == null){
                continue;
            }
            Brand brand = product.getBrand();

            FavoriteListDto dto = new FavoriteListDto();
            dto.setId(favorite.getId());
            dto.setProductId(product.getId());
            dto.setProductName(product.getProductName());
            dto.setMemberId(favorite.getMember().getId());
            dto.setMemberName(favorite.getMember().getNickname());
            dto.setCreateDate(favorite.getCreateDate());
            dto.setProductBrandName(brand.getEngName());

            dtoList.add(dto);
        }
        return dtoList;
    }


    @Override
    public void addEmitter(Long id, SseEmitter emitter) {
        emitters.put(id, emitter);
    }

    @Override
    public void removeEmitter(Long id) {
        emitters.remove(id);
    }

    @Override
    public void sendAlert(Long memberId, String message) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null){
            try{
                emitter.send(SseEmitter.event().data(message));
            }catch (IOException e){
                emitters.remove(memberId);
            }
        }
    }

    @Transactional
    @Override
    public void updateAlertPrice(Long favoriteId, Long memberId, Integer alertPrice) {

        Favorite favorite = favoriteRepository.findByIdAndMemberId(favoriteId,memberId)
                .orElseThrow(()-> new EntityNotFoundException("Favorite not found"));

        favorite.setAlertPrice(alertPrice);
        favoriteRepository.save(favorite);

    }


}
