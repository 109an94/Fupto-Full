package com.fupto.back.admin.board.dto;

import com.fupto.back.entity.Board;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String author;
    private Instant createdAt;
    private Instant modifiedAt;

    public BoardResponseDto(Board entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.author = entity.getAuthor();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
    }

}
