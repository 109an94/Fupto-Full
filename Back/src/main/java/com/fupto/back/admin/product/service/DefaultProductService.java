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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Resource getProductImage(Long id) throws IOException {
        ProductImage productImage = productImageRepository.findByProductIdAndDisplayOrder(id, 1)
                .orElseThrow(() -> new EntityNotFoundException("대표 이미지를 찾을 수 없습니다."));

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
    public ProductListDto update(ProductListDto productListDto) {
        Product product = productRepository.findById(productListDto.getId()).orElseThrow();

        if(productListDto.getProductName() != null)
            product.setProductName(productListDto.getProductName());
        if(productListDto.getRetailPrice() != null)
            product.setRetailPrice(productListDto.getRetailPrice());
        if(productListDto.getUrl() != null)
            product.setUrl(productListDto.getUrl());
        if(productListDto.getDescription() != null)
            product.setDescription(productListDto.getDescription());
        if(productListDto.getPresentId() != null)
            product.setPresentId(productListDto.getPresentId());
        if(productListDto.getMappingId() != null)
            product.setMappingId(productListDto.getMappingId());
        if(productListDto.getActive() != null)
            product.setActive(productListDto.getActive());
        if(productListDto.getState() != null)
            product.setState(productListDto.getState());

        productRepository.save(product);
        Product updateProduct = productRepository.findById(productListDto.getId()).orElseThrow();

        return modelMapper.map(updateProduct, ProductListDto.class);
    }

    // 일반 상품 삭제
    public void updateState(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        product.setState(false);
        product.setActive(false);
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
        product.setUpdateDate(Instant.now());
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
                        "http://localhost:8080/api/v1/admin/products/" + product.getId() + "/image"
                ));

        return productListDto;
    }
}
