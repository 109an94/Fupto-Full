package com.fupto.back.anonymous.product.service;

import com.fupto.back.anonymous.product.dto.*;
import com.fupto.back.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import com.fupto.back.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
    private FavoriteRepository favoriteRepository;
    private MemberRepository memberRepository;
    private ModelMapper modelMapper;

    private final Set<String> VALID_SORT_VALUES = Set.of("popular", "recent", "priceAsc", "priceDesc", "discountDesc");

    public DefaultProductService(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            PriceHistoryRepository priceHistoryRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            FavoriteRepository favoriteRepository,
            MemberRepository memberRepository,
            ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.favoriteRepository = favoriteRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public ProductResponseDto searchProducts(ProductSearchDto searchDto, Long memberId) {
        // 정렬 값 검증
        validateSort(searchDto.getSort());

        // 상품 조회
        List<Product> products = productRepository.searchProducts(
                searchDto.getGender(),
                searchDto.getCategory(),
                searchDto.getSub(),
                searchDto.getBrand(),
                searchDto.getShoppingmall(), //쇼핑몰 추가
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
                        Integer retailPriceOfLowestPrice = priceHistoryRepository.findRetailPriceOfLowestPriceProduct(product.getMappingId());

                        double discountRate = lowestPrice != null ?
                                ((retailPriceOfLowestPrice - lowestPrice) * 100.0) / retailPriceOfLowestPrice : 0.0;
                        return new ProductWithDiscountDto(product, discountRate);
                    })
                    .sorted(Comparator.comparing(ProductWithDiscountDto::getDiscountRate).reversed())
                    .map(ProductWithDiscountDto::getProduct)
                    .limit(searchDto.getLimit() + 1)
                    .collect(Collectors.toList());
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
                .map(product -> convertToProductListDto(product, memberId))
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
                .mappingId(product.getMappingId())
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

    private ProductListDto convertToProductListDto(Product product, Long memberId) {
        ProductListDto dto = convertToProductListDto(product);
        if (memberId != null) {
            dto.setFavorite(isFavorite(product.getMappingId(), memberId));
        }
        return dto;
    }

    private String getImageUrl(Long productId, Integer displayOrder) {
        return String.format("http://localhost:8085/api/v1/products/%d/image/%d", productId, displayOrder);
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
    public ProductDetailDto getById(Long id, Long memberId) {
        ProductDetailDto dto = getByIdInternal(id);  // 아래 private 메서드 호출
        if (memberId != null) {
            dto.setFavorite(isFavorite(dto.getMappingId(), memberId));
        }
        return dto;
    }

    private ProductDetailDto getByIdInternal(Long id) {
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
        // 4-1. 최저가 상품의 retailPrice
        Integer retailPriceOfLowestPrice = priceHistoryRepository.findRetailPriceOfLowestPriceProduct(product.getMappingId());


        // 5. 판매처별 최신 가격 정보를 포함한 ShopDto 리스트 생성
        List<Product> relatedProducts = productRepository
                .findAllByMappingIdAndStateIsTrue(product.getMappingId());

        List<ProductShopListDto> shops = relatedProducts.stream()
                .map(p -> {
                    Integer latestPrice = priceHistoryRepository.findLatestPriceByProductId(p.getId());
                    ShoppingMall mall = p.getShoppingMall();

                    return ProductShopListDto.builder()
                            .id(mall.getId())
                            .productId(p.getId())
                            .shopName(mall.getEngName())
                            .price(latestPrice)
                            .productUrl(p.getUrl())
                            .logoUrl(mall.getImg())
                            .deliveryFee(mall.getDeliveryfee())
                            .taxes(mall.getTaxes())
                            .build();
                })
                .filter(shop -> shop.getPrice() != null)
                .sorted(Comparator.comparing(ProductShopListDto::getPrice))
                .toList();

        // 6. 가격 정보 구성
        ProductPriceInfoDto priceInfo = ProductPriceInfoDto.builder()
                .retailPrice(retailPriceOfLowestPrice)
                .salePrice(lowestPrice)
                .discountRate(calculateDiscountRate(retailPriceOfLowestPrice, lowestPrice))
                .build();

        return ProductDetailDto.builder()
                .id(product.getId())
                .mappingId(product.getMappingId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .categories(categoryPath)
                .images(images)
                .brandEngName(product.getBrand().getEngName())
                .priceInfo(priceInfo)
                .shops(shops)
                .build();
    }

    @Override
    @Transactional
    public ProductDetailDto getSingleById(Long id, Long memberId) {
        ProductDetailDto dto = getSingleByIdInternal(id); // 아래 private 메서드 호출
        if (memberId != null) {
            dto.setFavorite(isFavorite(dto.getMappingId(), memberId));
        }
        return dto;
    }

    private ProductDetailDto getSingleByIdInternal(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        product.increaseViewCount();

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

        List<ProductImageDto> images = product.getProductImages().stream()
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder))
                .map(image -> ProductImageDto.builder()
                        .id(image.getId())
                        .displayOrder(image.getDisplayOrder())
                        .imageUrl(getImageUrl(product.getId(), image.getDisplayOrder()))
                        .build())
                .toList();

        Integer latestPrice = priceHistoryRepository.findLatestPriceByProductId(product.getId());

        ShoppingMall mall = product.getShoppingMall();
        List<ProductShopListDto> shops = List.of(
                ProductShopListDto.builder()
                        .id(mall.getId())
                        .productId(product.getId())
                        .shopName(mall.getEngName())
                        .price(latestPrice)
                        .productUrl(product.getUrl())
                        .logoUrl(mall.getImg())
                        .deliveryFee(mall.getDeliveryfee())
                        .taxes(mall.getTaxes())
                        .build()
        );

        ProductPriceInfoDto priceInfo = ProductPriceInfoDto.builder()
                .retailPrice(product.getRetailPrice())
                .salePrice(latestPrice)
                .discountRate(calculateDiscountRate(product.getRetailPrice(), latestPrice))
                .build();

        return ProductDetailDto.builder()
                .id(product.getId())
                .mappingId(product.getMappingId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .categories(categoryPath)
                .images(images)
                .brandEngName(product.getBrand().getEngName())
                .priceInfo(priceInfo)
                .shops(shops)
                .build();
    }



    @Override
    public ProductResponseDto getAllProductsByShoppingmall(ProductSearchDto searchDto, Long memberId) {
        // 정렬 값 검증
        validateSort(searchDto.getSort());

        List<Product> products = productRepository.findAllByShoppingmall(
                searchDto.getShoppingmall().get(0), // 쇼핑몰 ID는 리스트의 첫 번째 요소로 가정합니다.
                searchDto.getGender(),
                searchDto.getCategory(),
                searchDto.getSub(),
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
                        Integer retailPriceOfLowestPrice = priceHistoryRepository.findRetailPriceOfLowestPriceProduct(product.getMappingId());

                        double discountRate = lowestPrice != null ?
                                ((retailPriceOfLowestPrice - lowestPrice) * 100.0) / retailPriceOfLowestPrice : 0.0;
                        return new ProductWithDiscountDto(product, discountRate);
                    })
                    .sorted(Comparator.comparing(ProductWithDiscountDto::getDiscountRate).reversed())
                    .map(ProductWithDiscountDto::getProduct)
                    .limit(searchDto.getLimit() + 1)
                    .collect(Collectors.toList());
        }

        boolean hasMore = products.size() > searchDto.getLimit();
        if (hasMore) {
            products.remove(products.size() - 1);
        }

        Long nextCursor = hasMore ? products.get(products.size() - 1).getId() : null;

        List<ProductListDto> productDtos = products.stream()
                .map(product -> convertToProductListDto(product, memberId))
                .toList();

        return ProductResponseDto.builder()
                .products(productDtos)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long mappingId, Long memberId) {
        Optional<Favorite> existingFavorite = favoriteRepository
                .findByMemberIdAndMappingId(memberId, mappingId);

        if (existingFavorite.isPresent()) {
            // 이미 존재하면 상태 토글
            Favorite favorite = existingFavorite.get();
            favorite.setState(!favorite.getState());
            favorite.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
            return favorite.getState();
        } else {
            // 새로 생성
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

            Favorite favorite = Favorite.builder()
                    .mappingId(mappingId)
                    .member(member)
                    .state(true)
                    .build();

            favoriteRepository.save(favorite);
            return true;
        }
    }

    @Override
    public boolean isFavorite(Long mappingId, Long memberId) {
        return favoriteRepository.existsByMemberIdAndMappingIdAndStateIsTrue(memberId, mappingId);
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
}
