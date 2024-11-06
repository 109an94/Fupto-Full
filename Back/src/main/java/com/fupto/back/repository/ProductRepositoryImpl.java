//package com.fupto.Jpa.repository;
//
//import com.fupto.Jpa.admin.product.dto.CategoryInfo;
//import com.fupto.Jpa.admin.product.dto.ProductListDto;
//import com.fupto.Jpa.entity.Brand;
//import com.fupto.Jpa.entity.Category;
//import com.fupto.Jpa.entity.Product;
//import com.fupto.Jpa.entity.ShoppingMall;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Join;
//import jakarta.persistence.criteria.Root;
//
//import java.util.List;
//
//public class ProductRepositoryImpl implements ProductCustomRepository {
//    @PersistenceContext
//    private EntityManager em;
//
//    @Override
//    public List<ProductListDto> findAllProductsWithDetails() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<ProductListDto> cq = cb.createQuery(ProductListDto.class);
//        Root<Product> product = cq.from(Product.class);
////        Join<Product, Brand> brand = product.join("brand");
////        Join<Product, ShoppingMall> shoppingMall = product.join("shoppingMall");
////        Join<Product, Category> category = product.join("category");
////
////        cq.select(cb.construct(ProductListDto.class,
////                product,
////                brand.get("korName"),
////                shoppingMall.get("korName"),
////                cb.construct(CategoryInfo.class,
////                        category.get("id"),
////                        category.get("name"),
////                        category.get("level"))
////                ));
//        return em.createQuery(cq).getResultList();
//    }
//}
