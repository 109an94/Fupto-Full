package com.fupto.back.admin.brand.service;

import com.fupto.back.admin.brand.dto.BrandCreateDto;
import com.fupto.back.admin.brand.dto.BrandListDto;
import com.fupto.back.admin.brand.dto.BrandResponseDto;
import com.fupto.back.admin.brand.dto.BrandSearchDto;
import com.fupto.back.entity.Brand;
import com.fupto.back.entity.Product;
import com.fupto.back.repository.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service("adminBrandService")
public class DefaultBrandService implements BrandService {

    private BrandRepository brandRepository;
    private ModelMapper modelMapper;

    public DefaultBrandService(BrandRepository brandRepository, ModelMapper modelMapper) {
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandResponseDto getList(BrandSearchDto brandSearchDto) {
        Sort sort = Sort.by(
                brandSearchDto.getSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                brandSearchDto.getSortBy() != null ? brandSearchDto.getSortBy() : "createDate" // 기본값 설정
        );
        Pageable pageable = PageRequest.of(brandSearchDto.getPage() - 1, brandSearchDto.getSize(), sort);

        Instant startDate = null;
        Instant endDate = null;

        try {
            ZoneId zoneId = ZoneId.of("Asia/Seoul"); // 한국 표준시 (KST)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

            if (brandSearchDto.getStartDate() != null && !brandSearchDto.getStartDate().isEmpty()) {
                LocalDate localStartDate = LocalDate.parse(brandSearchDto.getStartDate(), DateTimeFormatter.ISO_DATE);
                // 시작일의 00:00:00을 한국 표준시로 설정 후 9시간 추가
                startDate = localStartDate.atStartOfDay(zoneId).plusHours(9).toInstant();
                System.out.println(formatter.format(startDate));
            }
            if (brandSearchDto.getEndDate() != null && !brandSearchDto.getEndDate().isEmpty()) {
                LocalDate localEndDate = LocalDate.parse(brandSearchDto.getEndDate(), DateTimeFormatter.ISO_DATE);
                // 종료일의 23:59:59를 한국 표준시로 설정 후 9시간 추가
                endDate = localEndDate.atTime(LocalTime.of(23, 59, 59)).atZone(zoneId).plusHours(9).toInstant();
                System.out.println(formatter.format(endDate));
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd format.", e);
        }

        // Repository 메서드 호출
        Page<Brand> brandPage = brandRepository.searchBrands(
                brandSearchDto.getName(),
                brandSearchDto.getNameType(),
                brandSearchDto.getActive(),
                startDate,
                endDate,
                brandSearchDto.getDateType(),
                pageable
        );

        List<BrandListDto> brandListDtos = brandPage
                .getContent()
                .stream()
                .map(brand -> {
                    BrandListDto brandListDto = modelMapper.map(brand, BrandListDto.class);

                    return brandListDto;
                }).toList();


        long totalElements = brandPage.getTotalElements();
        long totalPages = brandPage.getTotalPages();

        // 페이징 네이션 번호 리스트 생성
        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        return BrandResponseDto
                .builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNextPage(brandPage.hasNext())
                .hasPreviousPage(brandPage.hasPrevious())
                .pages(pages)
                .brands(brandListDtos)
                .build();
    }

    @Override
    public BrandListDto updateActive(Long id, Boolean active) {
        // 브랜드 조회
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("브랜드를 찾을 수 없습니다."));

        // 활성 상태 업데이트
        brand.setActive(active);
        brand.setUpdateDate(Instant.now());  // 갱신 날짜 설정

        // 변경된 브랜드 저장
        Brand updatedBrand = brandRepository.save(brand);

        // Builder 패턴을 사용하여 DTO 생성 후 반환
        return convertToBrandListDto(updatedBrand);
    }

    public BrandListDto create(BrandCreateDto brandCreateDto, MultipartFile file) {
        Brand brand = modelMapper.map(brandCreateDto, Brand.class);
        brand.setActive(brandCreateDto.getActive().equals("visible"));
        brand.setCreateDate(Instant.now());
        brand.setUpdateDate(Instant.now());

//        if (file != null && !file.isEmpty()) {
//            String fileName = saveFile(file);
//            brand.setImg(fileName);
//        }

        Brand savedBrand = brandRepository.save(brand);
        return convertToBrandListDto(savedBrand);
    }

//    private String saveFile(MultipartFile file) {
//        try {
//            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path filePath = Paths.get(uploadDir).resolve(fileName);
//            Files.copy(file.getInputStream(), filePath);
//            return fileName;
//        } catch (IOException e) {
//            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
//        }
//    }

    // ModelMapper를 사용하여 Brand 객체를 BrandListDto로 변환
    private BrandListDto convertToBrandListDto(Brand brand) {
        return modelMapper.map(brand, BrandListDto.class);
    }
}