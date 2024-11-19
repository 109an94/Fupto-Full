package com.fupto.back.admin.board.dto;

import lombok.*;

import javax.swing.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequestsDto {
   private String title;
   private String contents;
   private Boolean active;
   private Long boardCategoryId;
}
