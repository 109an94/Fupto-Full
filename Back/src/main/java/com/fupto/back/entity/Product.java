package com.fupto.back.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "retail_price", nullable = false)
    private Integer retailPrice;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "present_id")
    private Boolean presentId;

    @Column(name = "mapping_id")
    private Long mappingId;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "state")
    private Boolean state;

    @ColumnDefault("current_timestamp()")
    @Column(name = "create_date")
    private Instant createDate;

    @ColumnDefault("current_timestamp()")
    @Column(name = "update_date")
    private Instant updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonManagedReference
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonManagedReference
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shopping_mall_id", nullable = false)
    @JsonManagedReference
    private ShoppingMall shoppingMall;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PriceHistory> priceHistory;

}