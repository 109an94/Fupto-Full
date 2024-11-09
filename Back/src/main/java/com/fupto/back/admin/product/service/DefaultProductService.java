package com.fupto.back.admin.product.service;

import com.fupto.back.admin.product.dto.*;
import com.fupto.back.repository.BrandRepository;
import com.fupto.back.repository.ShoppingMallRepository;
import jakarta.persistence.EntityNotFoundException;
import com.fupto.back.entity.Category;
import com.fupto.back.entity.Product;
import com.fupto.back.repository.CategoryRepository;
import com.fupto.back.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service("adminProductService")
public class DefaultProductService implements ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private BrandRepository brandRepository;
    private ShoppingMallRepository shoppingMallRepository;
    private ModelMapper modelMapper;

    public DefaultProductService(ProductRepository productRepository,
                                 CategoryRepository categoryRepository,
                                 BrandRepository brandRepository,
                                 ShoppingMallRepository shoppingMallRepository,
                                 ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.shoppingMallRepository = shoppingMallRepository;
        this.modelMapper = modelMapper;
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
                searchDto.getMinPrice() != null || searchDto.getMaxPrice() != null) {
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
                    convertToInstant(searchDto.getStartDate()),
                    convertToInstant(searchDto.getEndDate()),
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
        return brandRepository.findByActiveIsTrue()
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
        return shoppingMallRepository.findByActiveIsTrue()
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
    public ProductListDto create(ProductListDto productListDto) {
        Product product = productRepository.save(modelMapper.map(productListDto, Product.class));
        Product newProduct = productRepository.findById(product.getId()).orElse(null);
        return modelMapper.map(newProduct, ProductListDto.class);
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

    private Instant convertToInstant(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
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
        productListDto.setSalePrice(product.getPriceHistories().isEmpty() ?
                null : product.getPriceHistories().getLast().getSalePrice());

        return productListDto;
    }
}
