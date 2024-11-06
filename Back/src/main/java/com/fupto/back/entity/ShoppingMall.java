package com.fupto.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "shopping_mall")
public class ShoppingMall {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "eng_name", nullable = false)
    private String engName;

    @Column(name = "kor_name", nullable = false)
    private String korName;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "img")
    private String img;

    @Column(name = "deliveryfee")
    private Integer deliveryfee;

    @Column(name = "taxes")
    private Integer taxes;

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

}