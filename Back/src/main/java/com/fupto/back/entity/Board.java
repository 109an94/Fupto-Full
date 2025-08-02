package com.fupto.back.entity;

import com.fupto.back.admin.board.dto.BoardRequestsDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

//    @Column(name = "password", nullable = true, length = 200)
//    private String password;

    @Column(name = "img")
    private String img;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reg_member_id", nullable = false)
    private Member regMember;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", insertable = false, updatable = false)
//    @ColumnDefault("current_timestamp()")
//    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("current_timestamp()")
    @Column(name = "modified_at", insertable = false)
//    @ColumnDefault("current_timestamp()")
//    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_category_id", nullable = false)
    private BoardCategory boardCategory;

    @Column(name = "active")
    private Boolean active;

    @PrePersist
    public void onPrePersist() {
        ZonedDateTime nowInKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.modifiedAt = nowInKST.toInstant();
    }

//    public void update(BoardRequestsDto requestsDto) {
//
//        this.title = requestsDto.getTitle();
//        this.contents = requestsDto.getContents();
////        this.password = requestsDto.getPassword();
//        this.modifiedAt = Instant.now().plusSeconds(9 * 3600);
//
//    }

}

