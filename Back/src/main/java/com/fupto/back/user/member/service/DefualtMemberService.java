package com.fupto.back.user.member.service;

import com.fupto.back.entity.Member;
import com.fupto.back.entity.ProductImage;
import com.fupto.back.repository.MemberRepository;
import com.fupto.back.repository.ProductImageRepository;
import com.fupto.back.repository.ProductRepository;
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
import java.util.Optional;

@Service("userMemberService")
@Transactional
public class DefualtMemberService implements MemberService {

    @Value("uploads")
    private String uploadPath;

    private final ProductImageRepository productImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public DefualtMemberService(MemberRepository memberRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.productImageRepository = productImageRepository;
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


}
