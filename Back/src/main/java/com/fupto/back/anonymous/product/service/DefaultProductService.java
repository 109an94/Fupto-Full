package com.fupto.back.anonymous.product.service;

import com.fupto.back.anonymous.product.dto.*;
import com.fupto.back.entity.Category;
import com.fupto.back.entity.ProductImage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import com.fupto.back.entity.Product;
import com.fupto.back.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultProductService implements ProductService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private ProductRepository productRepository;
    private ProductImageRepository productImageRepository;
    private PriceHistoryRepository priceHistoryRepository;
    private CategoryRepository categoryRepository;
    private BrandRepository brandRepository;
    private ModelMapper modelMapper;

    private final Set<String> VALID_SORT_VALUES = Set.of("popular", "recent", "priceAsc", "priceDesc", "discountDesc");

    public DefaultProductService(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            PriceHistoryRepository priceHistoryRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.modelMapper = modelMapper;
    }

    public ProductResponseDto searchProducts(ProductSearchDto searchDto) {
        // 정렬 값 검증
        validateSort(searchDto.getSort());

        // 상품 조회
        List<Product> products = productRepository.searchProducts(
                searchDto.getGender(),
                searchDto.getCat(),
                searchDto.getBrand(),
                searchDto.getMin(),
                searchDto.getMax(),
                searchDto.getCursor(),
                "discountDesc".equals(searchDto.getSort()) ? "popular" : searchDto.getSort(),
                PageRequest.of(0, searchDto.getLimit() + 1)
        );

        // 할인율 정렬이 필요한 경우
        if ("discountDesc".equals(searchDto.getSort())) {
            products = products.stream()
                    .map(product -> {
                        Integer lowestPrice = priceHistoryRepository.findLowestCurrentPrice(product.getMappingId());
                        double discountRate = lowestPrice != null ?
                                ((product.getRetailPrice() - lowestPrice) * 100.0) / product.getRetailPrice() : 0.0;
                        return new ProductWithDiscountDto(product, discountRate);
                    })
                    .sorted(Comparator.comparing(ProductWithDiscountDto::getDiscountRate).reversed())
                    .map(ProductWithDiscountDto::getProduct)
                    .limit(searchDto.getLimit() + 1)
                    .toList();
        }

        // 전체 결과 수 조회
//        Long totalCount = productRepository.countSearchResults(
//                searchDto.getGender(),
//                searchDto.getCat(),
//                searchDto.getBrand(),
//                searchDto.getMin(),
//                searchDto.getMax()
//        );

        // 다음 페이지 존재 여부 확인
        boolean hasMore = products.size() > searchDto.getLimit();
        if (hasMore) {
            products.remove(products.size() - 1);
        }

        // 다음 커서 설정
        Long nextCursor = hasMore ? products.get(products.size() - 1).getId() : null;

        // DTO 변환
        List<ProductListDto> productDtos = products.stream()
                .map(this::convertToProductListDto)
                .toList();

        return ProductResponseDto.builder()
                .products(productDtos)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
//                .totalCount(totalCount)
                .build();
    }

    private ProductListDto convertToProductListDto(Product product) {
        // 상품 기본 정보 매핑
        ProductListDto dto = ProductListDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .brandEngName(product.getBrand().getEngName())
                .viewCount(product.getViewCount())
                .build();

        // 최저가 설정
        Integer lowestPrice = priceHistoryRepository.findLowestCurrentPrice(product.getMappingId());
        dto.setSalePrice(lowestPrice);

        // 메인 이미지 설정
        productImageRepository.findByProductIdAndDisplayOrder(product.getId(), 1)
                .ifPresent(image -> dto.setMainImageUrl(getImageUrl(product.getId(), 1)));

        // 호버 이미지 설정
        productImageRepository.findByProductIdAndDisplayOrder(product.getId(), 2)
                .ifPresent(image -> dto.setHoverImageUrl(getImageUrl(product.getId(), 2)));

        return dto;
    }

    private String getImageUrl(Long productId, Integer displayOrder) {
        return String.format("http://localhost:8080/api/v1/products/%d/image/%d", productId, displayOrder);
    }

    public List<ProductCateDto> getCategories(Long parentId) {
        return categoryRepository.findByParentIdOrderByName(parentId)
                .stream()
                .map(category -> ProductCateDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    public List<ProductBrandDto> getBrands() {
        return brandRepository.findByActiveIsTrueOrderByEngNameAsc()
                .stream()
                .map(brand -> ProductBrandDto.builder()
                        .id(brand.getId())
                        .engName(brand.getEngName())
                        .korName(brand.getKorName())
                        .build())
                .toList();
    }

    @Override
    public Resource getProductImages(Long productId, Integer displayOrder) throws IOException {
        ProductImage productImage = productImageRepository.findByProductIdAndDisplayOrder(productId, displayOrder)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다."));

        Path imagePath = Paths.get(uploadPath, productImage.getFilePath());
        Resource resource = new FileSystemResource(imagePath.toFile());

        if (!resource.exists()) {
            throw new FileNotFoundException("이미지 파일을 찾을 수 없습니다.");
        }

        return resource;
    }

    @Override
    @Transactional
    public ProductDetailDto getById(Long id) {
        // 1. 상품 조회 및 조회수 증가
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        product.increaseViewCount();

        // 2. 카테고리 계층 구조 구성
        List<ProductCateDto> categoryPath = new ArrayList<>();
        Category currentCategory = product.getCategory();
        while (currentCategory != null) {
            categoryPath.add(0, ProductCateDto.builder()
                    .id(currentCategory.getId())
                    .name(currentCategory.getName())
                    .level(currentCategory.getLevel())
                    .build());
            currentCategory = currentCategory.getParent();
        }

        // 3. 상품 이미지 정보 구성
        List<ProductImageDto> images = product.getProductImages().stream()
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder))
                .map(image -> ProductImageDto.builder()
                        .id(image.getId())
                        .displayOrder(image.getDisplayOrder())
                        .imageUrl(getImageUrl(product.getId(), image.getDisplayOrder()))
                        .build())
                .toList();

        // 4. 최저가 계산 (mapping_id가 같은 모든 상품 중 최신 날짜의 최저가)
        Integer lowestPrice = priceHistoryRepository.findLowestCurrentPrice(product.getMappingId());

        // 5. 판매처별 최신 가격 정보를 포함한 ShopDto 리스트 생성
        List<Product> relatedProducts = productRepository
                .findAllByMappingIdAndStateIsTrue(product.getMappingId());

        List<ProductShopListDto> shops = relatedProducts.stream()
                .map(p -> {
                    Integer latestPrice = priceHistoryRepository.findLatestPriceByProductId(p.getId());

                    return ProductShopListDto
                            .builder()
                            .id(p.getShoppingMall().getId())
                            .productId(p.getId())
                            .shopName(p.getShoppingMall().getEngName())
                            .price(latestPrice)
                            .productUrl(p.getUrl())
                            .build();
                })
                .filter(shop -> shop.getPrice() != null)
                .sorted(Comparator.comparing(ProductShopListDto::getPrice))
                .toList();

        // 6. 가격 정보 구성
        ProductPriceInfoDto priceInfo = ProductPriceInfoDto.builder()
                .retailPrice(product.getRetailPrice())
                .salePrice(lowestPrice)
                .discountRate(calculateDiscountRate(product.getRetailPrice(), lowestPrice))
                .build();

        // 7. 최종 DTO 구성
        return ProductDetailDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .retailPrice(product.getRetailPrice())
                .description(product.getDescription())
                .categories(categoryPath)
                .images(images)
                .brandEngName(product.getBrand().getEngName())
                .priceInfo(priceInfo)
                .shops(shops)
                .build();
    }

    private Integer calculateDiscountRate(Integer retailPrice, Integer salePrice) {
        if (retailPrice == null || salePrice == null || retailPrice == 0) {
            return 0;
        }
        return (int)(((retailPrice - salePrice) * 100.0) / retailPrice);
    }

    private void validateSort(String sort) {
        if (!VALID_SORT_VALUES.contains(sort)) {
            throw new IllegalArgumentException("Invalid sort value: " + sort);
        }
    }

    @Transactional
    public void increaseViewCount(Long productId) {
        productRepository.findById(productId)
                .ifPresent(Product::increaseViewCount);
    }
}
