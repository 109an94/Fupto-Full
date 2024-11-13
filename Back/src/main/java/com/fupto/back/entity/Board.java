package com.fupto.back.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "author", nullable = false, length = 200)
    private String author;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reg_member_id", nullable = false)
    private Member regMember;

}