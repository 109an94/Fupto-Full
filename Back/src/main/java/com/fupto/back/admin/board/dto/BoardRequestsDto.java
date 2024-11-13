package com.fupto.back.admin.board.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardRequestsDto {
   private String title;
   private String contents;
   private String author;
   private String password;

}
