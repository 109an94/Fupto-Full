package com.fupto.back.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "kor_name", nullable = false)
    private String korName;

    @Column(name = "eng_name", nullable = false)
    private String engName;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "img", nullable = false)
    private String img;

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

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Product> products;

}