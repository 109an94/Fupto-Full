package com.fupto.back.admin.product.service;

import com.fupto.back.admin.product.dto.*;
import com.fupto.back.entity.PriceHistory;
import com.fupto.back.entity.ProductImage;
import com.fupto.back.repository.*;
import jakarta.persistence.EntityNotFoundException;
import com.fupto.back.entity.Category;
import com.fupto.back.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;

@Slf4j
@Service("adminProductService")
@Transactional
public class DefaultProductService implements ProductService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private BrandRepository brandRepository;
    private ShoppingMallRepository shoppingMallRepository;
    private PriceHistoryRepository priceHistoryRepository;
    private ProductImageRepository productImageRepository;
    private ModelMapper modelMapper;
    private FileService fileService;

    public DefaultProductService(ProductRepository productRepository,
                                 CategoryRepository categoryRepository,
                                 BrandRepository brandRepository,
                                 ShoppingMallRepository shoppingMallRepository,
                                 PriceHistoryRepository priceHistoryRepository,
                                    ProductImageRepository productImageRepository,
                                 ModelMapper modelMapper,
                                 FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.shoppingMallRepository = shoppingMallRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.productImageRepository = productImageRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductResponseDto getList(ProductSearchDto searchDto) {
        PageRequest pageable = PageRequest.of(
                searchDto.getPage(),
                searchDto.getSize(),
                Sort.by(searchDto.getSort()).descending()
        );

        Long category1 = searchDto.getCategory1();
        Long category2 = searchDto.getCategory2();
        Long category3 = searchDto.getCategory3();

        Page<Product> productPage;
        if (category1 != null || category2 != null || category3 != null ||
                searchDto.getActive() != null ||
                searchDto.getPresentId() != null ||
                (searchDto.getSearchKeyword() != null && !searchDto.getSearchKeyword().isEmpty()) ||
                searchDto.getMinPrice() != null || searchDto.getMaxPrice() != null ||
                searchDto.getStartDate() != null || searchDto.getEndDate() != null) {
            productPage = productRepository.findBySearchCriteria(
                    category1,
                    category2,
                    category3,
                    searchDto.getActive(),
                    searchDto.getPresentId(),
                    searchDto.getState(),
                    searchDto.getSearchType(),
                    searchDto.getSearchKeyword(),
                    searchDto.getMinPrice(),
                    searchDto.getMaxPrice(),
                    searchDto.getDateType(),
                    searchDto.getStartDate(),
                    searchDto.getEndDate(),
                    pageable
            );
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductListDto> productListDtos = productPage
                .getContent()
                .stream()
                .map(this::convertToProductListDto)
                .toList();

        return ProductResponseDto.builder()
                .products(productListDtos)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(productPage.getNumber())
                .pageSize(productPage.getSize())
                .build();
    }

    @Override
    public List<CategorySelectDto> getCategoriesByLevelAndParent(Integer level, Long parentId) {
        List<Category> categories;
        if (parentId != null) {
            categories = categoryRepository.findByLevelAndParentId(level, parentId);
        } else {
            categories = categoryRepository.findByLevel(level);
        }

        return categories
                .stream()
                .map(category -> CategorySelectDto
                        .builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    @Override
    public List<BrandSelectDto> getBrands() {
        return brandRepository.findByActiveIsTrueOrderByEngNameAsc()
                .stream()
                .map(brand -> BrandSelectDto
                        .builder()
                        .id(brand.getId())
                        .engName(brand.getEngName())
                        .korName(brand.getKorName())
                        .build())
                .toList();
    }

    @Override
    public List<ShoppingMallSelectDto> getShoppingMalls() {
        return shoppingMallRepository.findByActiveIsTrueOrderByEngNameAsc()
                .stream()
                .map(shop -> ShoppingMallSelectDto
                        .builder()
                        .id(shop.getId())
                        .engName(shop.getEngName())
                        .korName(shop.getKorName())
                        .build())
                .toList();
    }

    @Override
    public Resource getProductImage(Long id, Integer order) throws IOException {
        ProductImage productImage = productImageRepository.findByProductIdAndDisplayOrder(id, order)
                .orElseThrow(() -> new EntityNotFoundException("해당 순서의 이미지를 찾을 수 없습니다."));

        Path imagePath = Paths.get(uploadPath, productImage.getFilePath());
        Resource resource = new FileSystemResource(imagePath.toFile());

        if (!resource.exists()) {
            throw new FileNotFoundException("이미지 파일을 찾을 수 없습니다.");
        }

        return resource;
    }

    @Override
    public List<ProductListDto> getMappingProducts(Long mappingId) {
        List<Product> products = productRepository.findAllByMappingIdAndStateIsTrue(mappingId);

        return products
                .stream()
                .filter(product -> product.getPresentId() == false)
                .map(this::convertToProductListDto)
                .toList();
    }

    @Override
    public ProductListDto getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        ProductListDto productListDto = modelMapper.map(product, ProductListDto.class);
        return productListDto;
    }

    @Override
    public List<ProductListDto> create(List<ProductRegDto> regDtos,
                                       Map<Integer, List<MultipartFile>> filesMap,
                                       Map<Integer, Map<String, MultipartFile>> filesByNameMap) {
        List<ProductListDto> results = new ArrayList<>();

        ProductRegDto representativeDto = regDtos.stream()
                .filter(dto -> dto.getPresentId())
                .findFirst()
                .orElse(null);

        if (representativeDto != null) {
            Product representativeProduct = createSingleProduct(
                    representativeDto,
                    filesMap.get(representativeDto.getFormId()),
                    filesByNameMap.get(representativeDto.getFormId()),
                    null
            );

            representativeProduct.setMappingId(representativeProduct.getId());
            representativeProduct = productRepository.save(representativeProduct);
            results.add(convertToProductListDto(representativeProduct));

            for (ProductRegDto regDto : regDtos) {
                if (!regDto.getPresentId()) {
                    Product product = createSingleProduct(
                            regDto,
                            filesMap.get(regDto.getFormId()),
                            filesByNameMap.get(regDto.getFormId()),
                            representativeProduct.getId()
                    );
                    results.add(convertToProductListDto(product));
                }
            }
        } else {
            for (ProductRegDto regDto : regDtos) {
                Product product = createSingleProduct(
                        regDto,
                        filesMap.get(regDto.getFormId()),
                        filesByNameMap.get(regDto.getFormId()),
                        null
                );
                results.add(convertToProductListDto(product));
            }
        }

        return results;
    }

    @Transactional
    public void createMapping(ProductMappingDto request) {
        // 새로운 대표상품이 될 상품 조회
        Product newMainProduct = productRepository.findById(request.getMainProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 선택된 상품들 조회
        List<Product> selectedProducts = productRepository.findAllById(request.getMappingProductIds());

        // 같은 매핑 그룹 내의 변경인지 확인
        boolean isSameGroup = false;
        Product currentMainProduct;

        if (newMainProduct.getMappingId() != null) {
            // 선택된 상품이 매핑상품인 경우, 해당 그룹의 대표상품 찾기
            currentMainProduct = productRepository.findById(newMainProduct.getMappingId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

            // 선택된 모든 상품이 같은 매핑 그룹인지 확인
            isSameGroup = selectedProducts.stream()
                    .allMatch(product ->
                            product.getMappingId() != null &&
                                    product.getMappingId().equals(currentMainProduct.getId()));
        } else {
            currentMainProduct = null;
        }

        if (isSameGroup) {
            // 같은 매핑 그룹 내에서의 변경
            handleSameGroupMapping(newMainProduct, selectedProducts, currentMainProduct);
        } else {
            // 다른 매핑 그룹들 간의 변경
            handleDifferentGroupMapping(newMainProduct, selectedProducts);
        }
    }

    private void handleSameGroupMapping(Product newMainProduct, List<Product> selectedProducts, Product currentMainProduct) {
        // 새로운 대표상품 설정
        newMainProduct.setPresentId(true);
        newMainProduct.setMappingId(newMainProduct.getId());
        newMainProduct.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
        productRepository.save(newMainProduct);

        // 현재 대표상품도 매핑상품으로 변경
        currentMainProduct.setPresentId(false);
        currentMainProduct.setMappingId(newMainProduct.getId());
        currentMainProduct.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
        productRepository.save(currentMainProduct);

        // 나머지 매핑상품들 처리
        List<Product> mappingProducts = productRepository.findAllByMappingIdAndStateTrue(currentMainProduct.getId());
        for (Product product : mappingProducts) {
            if (!product.getId().equals(newMainProduct.getId())) {
                product.setMappingId(newMainProduct.getId());
                product.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
            }
        }
        productRepository.saveAll(mappingProducts);
    }

    private void handleDifferentGroupMapping(Product newMainProduct, List<Product> selectedProducts) {
        // 새로운 대표상품 설정
        newMainProduct.setPresentId(true);
        newMainProduct.setMappingId(newMainProduct.getId());
        newMainProduct.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
        productRepository.save(newMainProduct);

        // 선택된 상품들 처리
        for (Product selectedProduct : selectedProducts) {
            if (selectedProduct.getPresentId()) {
                // 대표상품의 모든 매핑상품들을 새로운 대표상품의 매핑상품으로 설정
                List<Product> mappingProducts = productRepository.findAllByMappingIdAndStateTrue(selectedProduct.getId());
                for (Product product : mappingProducts) {
                    product.setMappingId(newMainProduct.getId());
                    product.setPresentId(false);
                    product.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
                }
                productRepository.saveAll(mappingProducts);
            }

            // 선택된 상품을 새로운 매핑상품으로 설정
            selectedProduct.setPresentId(false);
            selectedProduct.setMappingId(newMainProduct.getId());
            selectedProduct.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
            productRepository.save(selectedProduct);
        }
    }

    private Product createSingleProduct(ProductRegDto regDto,
                                        List<MultipartFile> files,
                                        Map<String, MultipartFile> filesByName,
                                        Long mappingId) {
        Product product = Product.builder()
                .productName(regDto.getProductName())
                .retailPrice(regDto.getRetailPrice())
                .url(regDto.getUrl())
                .description(regDto.getDescription())
                .presentId(regDto.getPresentId())
                .mappingId(mappingId)
                .active(regDto.getActive())
                .state(true)
                .category(categoryRepository.findById(regDto.getCategoryId()).orElseThrow())
                .brand(brandRepository.findById(regDto.getBrandId()).orElseThrow())
                .shoppingMall(shoppingMallRepository.findById(regDto.getShoppingMallId()).orElseThrow())
                .productImages(new ArrayList<>())
                .build();

        product = productRepository.save(product);

        PriceHistory priceHistory = PriceHistory.builder()
                .product(product)
                .salePrice(regDto.getSalePrice())
                .build();
        priceHistoryRepository.save(priceHistory);

        // 순서대로 이미지 저장
        if (regDto.getImageFileNames() != null && !regDto.getImageFileNames().isEmpty()) {
            for (int i = 0; i < regDto.getImageFileNames().size(); i++) {
                String fileName = regDto.getImageFileNames().get(i);
                MultipartFile file = filesByName.get(fileName);

                if (file != null) {
                    try {
                        String filePath = fileService.saveFile(file, product.getId());
                        ProductImage image = ProductImage.builder()
                                .product(product)
                                .fileName(filePath.substring(filePath.lastIndexOf("/") + 1))
                                .originalName(file.getOriginalFilename())
                                .filePath(filePath)
                                .displayOrder(i + 1)
                                .build();
                        product.getProductImages().add(image);
                    } catch (IOException e) {
                        log.error("Failed to save file", e);
                    }
                }
            }
        }

        return product;
    }

    @Override
    public ProductUpdateDto getProductForEdit(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 카테고리 계층 구조 가져오기
        Category thirdCategory = product.getCategory();
        Category secondCategory = thirdCategory.getParent();
        Category firstCategory = secondCategory.getParent();

        // 최근 가격 조회
        Integer latestPrice = priceHistoryRepository.findLatestPriceByProductId(product.getId());

        // 이미지 정보 변환
        List<ProductImageDto> imageDtos = product.getProductImages().stream()
                .map(image -> ProductImageDto.builder()
                        .id(image.getId())
                        .originalName(image.getOriginalName())
                        .filePath(image.getFilePath())
                        .displayOrder(image.getDisplayOrder())
                        .build())
                .sorted(Comparator.comparing(ProductImageDto::getDisplayOrder))
                .toList();

        return ProductUpdateDto.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .retailPrice(product.getRetailPrice())
                .url(product.getUrl())
                .description(product.getDescription())
                .presentId(product.getPresentId())
                .mappingId(product.getMappingId())
                .active(product.getActive())
                .category1Id(firstCategory.getId())
                .category2Id(secondCategory.getId())
                .category3Id(thirdCategory.getId())
                .brandId(product.getBrand().getId())
                .shoppingMallId(product.getShoppingMall().getId())
                .salePrice(latestPrice)
                .productImages(imageDtos)
                .build();
    }

    @Override
    @Transactional
    public ProductListDto update(Long id, ProductUpdateRequestDto updateDto, List<MultipartFile> files) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 1. 기본 정보 업데이트
        updateBasicInfo(product, updateDto);

        // 2. 가격 업데이트 (변경된 경우에만)
        if (!Objects.equals(updateDto.getSalePrice(),
                priceHistoryRepository.findLatestPriceByProductId(id))) {
            PriceHistory priceHistory = PriceHistory.builder()
                    .product(product)
                    .salePrice(updateDto.getSalePrice())
                    .build();
            priceHistoryRepository.save(priceHistory);
        }

        // 3. 이미지 처리
        updateImages(product, updateDto.getExistingImageIds(), updateDto.getImageOrders(), files);

        productRepository.save(product);
        return convertToProductListDto(product);
    }

    private void updateBasicInfo(Product product, ProductUpdateRequestDto updateDto) {
        product.setProductName(updateDto.getProductName());
        product.setRetailPrice(updateDto.getRetailPrice());
        product.setUrl(updateDto.getUrl());
        product.setDescription(updateDto.getDescription());
        product.setPresentId(updateDto.getPresentId());
        product.setActive(updateDto.getActive());

        // 카테고리, 브랜드, 쇼핑몰 업데이트
        product.setCategory(categoryRepository.findById(updateDto.getCategory3Id())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다.")));
        product.setBrand(brandRepository.findById(updateDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("브랜드를 찾을 수 없습니다.")));
        product.setShoppingMall(shoppingMallRepository.findById(updateDto.getShoppingMallId())
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰을 찾을 수 없습니다.")));

        product.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
    }

    private void updateImages(Product product, List<Long> existingImageIds,
                              List<ImageOrderDto> imageOrders, List<MultipartFile> newFiles) {
        // 1. 삭제된 이미지 처리
        List<ProductImage> currentImages = product.getProductImages();
        List<ProductImage> toDelete = currentImages.stream()
                .filter(img -> !existingImageIds.contains(img.getId()))
                .toList();

        // 파일 시스템에서 삭제
        toDelete.forEach(image -> {
            try {
                Files.deleteIfExists(Paths.get(uploadPath, image.getFilePath()));
            } catch (IOException e) {
                log.error("Failed to delete image file: {}", image.getFilePath(), e);
            }
        });

        // DB에서 삭제
        productImageRepository.deleteAll(toDelete);
        currentImages.removeAll(toDelete);

        // 2. 기존 이미지 순서 업데이트
        imageOrders.stream()
                .filter(order -> !order.getIsNew())
                .forEach(order -> {
                    ProductImage image = product.getProductImages().stream()
                            .filter(img -> img.getId().equals(order.getId()))
                            .findFirst()
                            .orElseThrow();
                    image.setDisplayOrder(order.getDisplayOrder());
                });

        // 3. 새 이미지 저장
        if (newFiles != null && !newFiles.isEmpty()) {
            imageOrders.stream()
                    .filter(ImageOrderDto::getIsNew)
                    .forEach(order -> {
                        int newFileIndex = order.getDisplayOrder() - 1 - existingImageIds.size();
                        if (newFileIndex >= 0 && newFileIndex < newFiles.size()) {
                            MultipartFile file = newFiles.get(newFileIndex);
                            try {
                                String filePath = fileService.saveFile(file, product.getId());
                                ProductImage image = ProductImage.builder()
                                        .product(product)
                                        .fileName(filePath.substring(filePath.lastIndexOf("/") + 1))
                                        .originalName(file.getOriginalFilename())
                                        .filePath(filePath)
                                        .displayOrder(order.getDisplayOrder())
                                        .build();
                                product.getProductImages().add(image);
                            } catch (IOException e) {
                                log.error("Failed to save new image file", e);
                                throw new RuntimeException("이미지 저장 실패", e);
                            }
                        }
                    });
        }
    }

    // 일반 상품 삭제
    public void updateState(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        product.setState(false);
        product.setActive(false);
        product.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
        productRepository.save(product);
    }

    // 대표상품 삭제 및 매핑상품 승격
    public void promoteAndDelete(Long mainProductId) {
        // 1. 대표상품 조회
        Product mainProduct = productRepository.findById(mainProductId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        System.out.println("대표상품 조회: " + mainProduct.getId());

        // 2. 매핑된 상품들 조회 (state가 true인 것만)
        List<Product> mappingProducts = productRepository.findAllByMappingIdAndStateTrue(mainProductId);
        System.out.println("매핑된 상품 개수: " + mappingProducts.size());
        mappingProducts.forEach(p ->
                System.out.println("매핑상품 정보: id=" + p.getId() +
                        ", presentId=" + p.getPresentId() +
                        ", mappingId=" + p.getMappingId())
        );

        if (!mappingProducts.isEmpty()) {
            // 3. 첫 번째 매핑상품을 대표상품으로 승격
            Product newMainProduct = mappingProducts.get(0);
            newMainProduct.setPresentId(true);
            newMainProduct.setMappingId(newMainProduct.getId());
            System.out.println("새로운 대표상품: id=" + newMainProduct.getId() +
                    ", presentId=" + newMainProduct.getPresentId() +
                    ", mappingId=" + newMainProduct.getMappingId());
            productRepository.save(newMainProduct);

            // 4. 나머지 매핑상품들의 mappingId를 새로운 대표상품 ID로 변경
            if (mappingProducts.size() > 1) {
                for (int i = 1; i < mappingProducts.size(); i++) {
                    Product mappingProduct = mappingProducts.get(i);
                    mappingProduct.setMappingId(newMainProduct.getId());
                    System.out.println("매핑상품 수정: id=" + mappingProduct.getId() +
                            ", mappingId=" + mappingProduct.getMappingId());
                }
                productRepository.saveAll(mappingProducts.subList(1, mappingProducts.size()));
            }
        } else {
            System.out.println("매핑된 상품이 없습니다!");
        }

        // 5. 기존 대표상품 상태 변경
        mainProduct.setPresentId(false);
        mainProduct.setMappingId(null);
        mainProduct.setState(false);
        mainProduct.setActive(false);
        productRepository.save(mainProduct);
        System.out.println("기존 대표상품 상태 변경 완료");
    }


    @Override
    public ProductListDto updateActive(Long id, Boolean active) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        product.setActive(active);
        product.setUpdateDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));
        product = productRepository.save(product);

        return convertToProductListDto(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private ProductListDto convertToProductListDto(Product product) {
        ProductListDto productListDto = modelMapper.map(product, ProductListDto.class);

        Category thirdCategory = product.getCategory();
        Category secondCategory = thirdCategory.getParent();
        Category firstCategory = secondCategory.getParent();

        String categoryPath = (firstCategory.getName() + " > ") +
                (secondCategory.getName() + " > ") +
                thirdCategory.getName();

        productListDto.setBrandEngName(product.getBrand().getEngName());
        productListDto.setCategoryName(categoryPath);
        productListDto.setShoppingMallEngName(product.getShoppingMall().getEngName());

        // 가장 최근 가격만 조회
        Integer latestPrice = priceHistoryRepository.findLatestPriceByProductId(product.getId());
        productListDto.setSalePrice(latestPrice);

        product.getProductImages().stream()
                .filter(image -> image.getDisplayOrder() == 1)
                .findFirst()
                .ifPresent(image -> productListDto.setImagePath(
                        "http://localhost:8080/api/v1/admin/products/" + product.getId() + "/image/1"
                ));

        return productListDto;
    }
}
