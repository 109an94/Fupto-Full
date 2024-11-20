package com.fupto.back.admin.shoppingmall.service;

import com.fupto.back.admin.brand.dto.BrandListDto;
import com.fupto.back.admin.brand.dto.BrandUpdateDto;
import com.fupto.back.admin.shoppingmall.dto.*;
import com.fupto.back.entity.Brand;
import com.fupto.back.entity.ShoppingMall;
import com.fupto.back.repository.ShoppingMallRepository;
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

@Service("adminShoppingmallService")
public class DefaultShoppingmallSerivce implements ShoppingmallSerivce {

    @Value("${file.upload.path}")
    private String uploadPath;

    private ShoppingMallRepository shoppingMallRepository;
    private ModelMapper modelMapper;

    public DefaultShoppingmallSerivce(ShoppingMallRepository shoppingMallRepository, ModelMapper modelMapper) {
        this.shoppingMallRepository = shoppingMallRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShoppingmallResponseDto getList(ShoppingmallSearchDto shoppingmallSearchDto) {
        Sort sort = Sort.by(
                shoppingmallSearchDto.getSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                shoppingmallSearchDto.getSortBy() != null ? shoppingmallSearchDto.getSortBy() : "createDate" // 기본값 설정
        );
        Pageable pageable = PageRequest.of(shoppingmallSearchDto.getPage() - 1, shoppingmallSearchDto.getSize(), sort);

        Instant startDate = null;
        Instant endDate = null;

        try {
            ZoneId zoneId = ZoneId.of("Asia/Seoul"); // 한국 표준시 (KST)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

            if (shoppingmallSearchDto.getStartDate() != null && !shoppingmallSearchDto.getStartDate().isEmpty()) {
                LocalDate localStartDate = LocalDate.parse(shoppingmallSearchDto.getStartDate(), DateTimeFormatter.ISO_DATE);
                // 시작일의 00:00:00을 한국 표준시로 설정 후 9시간 추가
                startDate = localStartDate.atStartOfDay(zoneId).plusHours(9).toInstant();
                System.out.println(formatter.format(startDate));
            }
            if (shoppingmallSearchDto.getEndDate() != null && !shoppingmallSearchDto.getEndDate().isEmpty()) {
                LocalDate localEndDate = LocalDate.parse(shoppingmallSearchDto.getEndDate(), DateTimeFormatter.ISO_DATE);
                // 종료일의 23:59:59를 한국 표준시로 설정 후 9시간 추가
                endDate = localEndDate.atTime(LocalTime.of(23, 59, 59)).atZone(zoneId).plusHours(9).toInstant();
                System.out.println(formatter.format(endDate));
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd format.", e);
        }

        // Repository 메서드 호출
        Page<ShoppingMall> shoppingmallPage = shoppingMallRepository.searchShoppingmalls(
                shoppingmallSearchDto.getName(),
                shoppingmallSearchDto.getNameType(),
                shoppingmallSearchDto.getActive(),
                startDate,
                endDate,
                shoppingmallSearchDto.getDateType(),
                pageable
        );

        List<ShoppingmallListDto> shoppingmallListDtos = shoppingmallPage
                .getContent()
                .stream()
                .map(shoppingmall -> {
                    ShoppingmallListDto shoppingmallListDto = modelMapper.map(shoppingmall, ShoppingmallListDto.class);

                    return shoppingmallListDto;
                }).toList();


        long totalElements = shoppingmallPage.getTotalElements();
        long totalPages = shoppingmallPage.getTotalPages();

        // 페이징 네이션 번호 리스트 생성
        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        return ShoppingmallResponseDto
                .builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNextPage(shoppingmallPage.hasNext())
                .hasPreviousPage(shoppingmallPage.hasPrevious())
                .pages(pages)
                .shoppingmalls(shoppingmallListDtos)
                .build();
    }

    @Override
    public ShoppingmallListDto updateActive(Long id, Boolean active) {
        // 쇼핑몰 조회
        ShoppingMall shoppingmall = shoppingMallRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰을 찾을 수 없습니다."));

        // 활성 상태 업데이트
        shoppingmall.setActive(active);
        shoppingmall.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));  // 갱신 날짜 설정

        // 변경된 쇼핑몰 저장
        ShoppingMall updatedShoppingmall = shoppingMallRepository.save(shoppingmall);

        // Builder 패턴을 사용하여 DTO 생성 후 반환
        return convertToShoppingmallListDto(updatedShoppingmall);
    }

    @Override
    public ShoppingmallListDto createShoppingmall(ShoppingmallCreateDto shoppingmallCreateDto, MultipartFile file) throws IOException{
        //  정보 매핑
        ShoppingMall shoppingMall = modelMapper.map(shoppingmallCreateDto, ShoppingMall.class);

        // 쇼핑몰 기본 상태 설정
        shoppingMall.setState(true);
//        shoppingMall.setCreateDate(Instant.now());
//        shoppingMall.setUpdateDate(Instant.now());
        shoppingMall.setImg("");

        // 먼저 쇼핑몰을 저장하여 ID를 생성
        ShoppingMall savedShoppingmall = shoppingMallRepository.save(shoppingMall);
        Long shoppingmallId = savedShoppingmall.getId();

        // 파일이 비어있지 않다면 저장
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, shoppingmallId); // shoppingmallId를 넘겨 경로에 포함시킴
            savedShoppingmall.setImg(fileName); // 파일명 설정
        }

        // 변경사항 저장
        savedShoppingmall = shoppingMallRepository.save(savedShoppingmall);

        // DTO로 변환하여 반환
        return convertToShoppingmallListDto(savedShoppingmall);
    }

    public ShoppingmallListDto updateState(Long id, boolean state) {
        ShoppingMall shoppingMall = shoppingMallRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shoppingmall not found with id: " + id));

        shoppingMall.setActive(state);
        shoppingMall.setState(state);
        ShoppingMall updatedShoppingmall = shoppingMallRepository.save(shoppingMall);

        return convertToShoppingmallListDto(updatedShoppingmall);
    }

    public void bulkUpdateState(List<Long> shoppingmallIds, boolean state) {
        List<ShoppingMall> shoppingMallsToUpdate = shoppingMallRepository.findAllById(shoppingmallIds);
        for (ShoppingMall shoppingMall : shoppingMallsToUpdate) {
            shoppingMall.setActive(state);
            shoppingMall.setState(state);
        }
        shoppingMallRepository.saveAll(shoppingMallsToUpdate);
    }

    @Override
    public ShoppingmallListDto show(Long id) {
        ShoppingMall shoppingmall = shoppingMallRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shoppingmall not found with id: " + id));

        return ShoppingmallListDto.builder()
                .korName(shoppingmall.getKorName())
                .engName(shoppingmall.getEngName())
                .url(shoppingmall.getUrl())
                .active(shoppingmall.getActive())
                .description(shoppingmall.getDescription())
                .img(shoppingmall.getImg())
                .deliveryfee(shoppingmall.getDeliveryfee())
                .taxes(shoppingmall.getTaxes())
                .build();
    }

    @Override
    public ShoppingmallListDto update(Long id, ShoppingmallUpdateDto shoppingmallUpdateDto, MultipartFile file) throws IOException {
        ShoppingMall shoppingmall = shoppingMallRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shoppingmall not found with id: " + id));

        // 브랜드 정보 업데이트
        shoppingmall.setKorName(shoppingmallUpdateDto.getKorName());
        shoppingmall.setEngName(shoppingmallUpdateDto.getEngName());
        shoppingmall.setUrl(shoppingmallUpdateDto.getUrl());
        shoppingmall.setActive(shoppingmallUpdateDto.getActive());
        shoppingmall.setDescription(shoppingmallUpdateDto.getDescription());
        shoppingmall.setDeliveryfee((shoppingmallUpdateDto.getDeliveryfee()));
        shoppingmall.setTaxes((shoppingmallUpdateDto.getTaxes()));

        // 파일 처리
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, id);
            shoppingmall.setImg(fileName);
        }

        // 수정 날짜 업데이트
        shoppingmall.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));

        // 브랜드 저장
        ShoppingMall updatedShoppingmall = shoppingMallRepository.save(shoppingmall);

        // BrandListDto로 변환하여 반환
        return convertToShoppingmallListDto(updatedShoppingmall);
    }

    private String saveFile(MultipartFile file, Long shoppingmallId) throws IOException {
        // 파일 이름에 UUID 추가
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 브랜드 ID를 포함한 디렉토리 생성
        Path shoppingmallUploadDir = Paths.get(uploadPath, "shoppingmalls", shoppingmallId.toString());
        if (!Files.exists(shoppingmallUploadDir)) {
            Files.createDirectories(shoppingmallUploadDir);
        }

        // 최종 파일 경로 설정
        Path filePath = shoppingmallUploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 경로가 포함된 파일명을 반환 (백슬래시를 포워드 슬래시로 변경)
        return filePath.toString().replace("\\", "/");
    }

    private ShoppingmallListDto convertToShoppingmallListDto(ShoppingMall shoppingmall) {
        return modelMapper.map(shoppingmall, ShoppingmallListDto.class);
    }
}
