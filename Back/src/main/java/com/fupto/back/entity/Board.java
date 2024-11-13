package com.fupto.back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "contents", nullable = false, length = 1000)
    private String contents;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_member_id")
    private Member regMember;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("current_timestamp()")
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_category_id", nullable = false)
    private BoardCategory boardCategory;

    @Column(name = "active", nullable = false)
    private Boolean active = false;

}