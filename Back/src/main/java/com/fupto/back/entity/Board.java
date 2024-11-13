package com.fupto.back.entity;

import com.fupto.back.admin.board.dto.BoardRequestsDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
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

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "reg_member_id",nullable = true)
//    private Member regMember;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("current_timestamp()")
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.modifiedAt == null) {
            this.modifiedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedAt = Instant.now();  // 수정 시에만 modifiedAt 갱신
    }

    public void update(BoardRequestsDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
    }

    public Board (BoardRequestsDto requestsDto) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        this.author = requestsDto.getAuthor();
        this.password = requestsDto.getPassword();
    }
}

