package com.fupto.back.admin.brand.service;

import com.fupto.back.admin.brand.dto.*;
import com.fupto.back.entity.Brand;
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
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("adminBrandService")
public class DefaultBrandService implements BrandService {

    @Value("${file.upload.path}")
    private String uploadPath;

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
        brand.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));  // 갱신 날짜 설정

        // 변경된 브랜드 저장
        Brand updatedBrand = brandRepository.save(brand);

        // Builder 패턴을 사용하여 DTO 생성 후 반환
        return convertToBrandListDto(updatedBrand);
    }

    @Override
    public BrandListDto createBrand(BrandCreateDto brandCreateDto, MultipartFile file) throws IOException {
        // 브랜드 정보 매핑
        Brand brand = modelMapper.map(brandCreateDto, Brand.class);

        // 브랜드 기본 상태 설정
        brand.setState(true);
//        brand.setCreateDate(Instant.now());
//        brand.setUpdateDate(Instant.now());
        brand.setImg("-");

        // 먼저 브랜드를 저장하여 ID를 생성
        Brand savedBrand = brandRepository.save(brand);
        Long brandId = savedBrand.getId();

        // 파일이 비어있지 않다면 저장
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, brandId); // brandId를 넘겨 경로에 포함시킴
            savedBrand.setImg(fileName); // 파일명 설정
        }

        // 변경사항 저장
        savedBrand = brandRepository.save(savedBrand);

        // DTO로 변환하여 반환
        return convertToBrandListDto(savedBrand);
    }

    public BrandListDto updateState(Long id, boolean state) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + id));

        brand.setActive(state);
        brand.setState(state);
        Brand updatedBrand = brandRepository.save(brand);

        return convertToBrandListDto(updatedBrand);
    }

    public void bulkUpdateState(List<Long> brandIds, boolean state) {
        List<Brand> brandsToUpdate = brandRepository.findAllById(brandIds);
        for (Brand brand : brandsToUpdate) {
            brand.setActive(state);
            brand.setState(state);
        }
        brandRepository.saveAll(brandsToUpdate);
    }

    @Override
    public BrandListDto show(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + id));

        return BrandListDto.builder()
                .korName(brand.getKorName())
                .engName(brand.getEngName())
                .url(brand.getUrl())
                .active(brand.getActive())
                .description(brand.getDescription())
                .img(brand.getImg())
                .build();
    }

    @Override
    public BrandListDto update(Long id, BrandUpdateDto brandUpdateDto, MultipartFile file) throws IOException {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id: " + id));

        // 브랜드 정보 업데이트
        brand.setKorName(brandUpdateDto.getKorName());
        brand.setEngName(brandUpdateDto.getEngName());
        brand.setUrl(brandUpdateDto.getUrl());
        brand.setActive(brandUpdateDto.getActive());
        brand.setDescription(brandUpdateDto.getDescription());

        // 파일 처리
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, id);
            brand.setImg(fileName);
        }

        // 수정 날짜 업데이트
        brand.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));

        // 브랜드 저장
        Brand updatedBrand = brandRepository.save(brand);

        // BrandListDto로 변환하여 반환
        return convertToBrandListDto(updatedBrand);
    }

    private String saveFile(MultipartFile file, Long brandId) throws IOException {
        // 파일 이름에 UUID 추가
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 브랜드 ID를 포함한 디렉토리 생성
        Path brandUploadDir = Paths.get(uploadPath, "brands", brandId.toString());
        if (!Files.exists(brandUploadDir)) {
            Files.createDirectories(brandUploadDir);
        }

        // 최종 파일 경로 설정
        Path filePath = brandUploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 경로가 포함된 파일명을 반환 (백슬래시를 포워드 슬래시로 변경)
        return filePath.toString().replace("\\", "/");
    }

    // ModelMapper를 사용하여 Brand 객체를 BrandListDto로 변환
    private BrandListDto convertToBrandListDto(Brand brand) {
        return modelMapper.map(brand, BrandListDto.class);
    }
}