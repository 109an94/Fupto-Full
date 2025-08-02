package com.fupto.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shopping_mall")
public class ShoppingMall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "create_date", insertable = false, updatable = false)
    private Instant createDate;

    @ColumnDefault("current_timestamp()")
    @Column(name = "update_date", insertable = false)
    private Instant updateDate;
}