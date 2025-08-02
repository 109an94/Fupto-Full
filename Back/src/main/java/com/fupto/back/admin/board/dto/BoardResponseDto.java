package com.fupto.back.admin.board.dto;

import com.fupto.back.entity.Board;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private Boolean active;
    private Instant createdAt;
    private Instant modifiedAt;
    private Long boardCategoryId;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.active = board.getActive();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.boardCategoryId= board.getBoardCategory().getId();
    }

}
