//package com.fupto.fupto.admin.board.mapper;
//
//import com.fupto.fupto.admin.board.dto.BoardListDto;
//import com.fupto.fupto.entity.Board;
//
//public class BoardMapper {
//
//    public static BoardListDto mapToDto(Board board) {
//        if (board == null)
//            return null;
//
//        return BoardListDto
//                .builder()
//                .Id(board.getId())
//                .title(board.getTitle())
//                .content(board.getContent())
//                .boardCategoryId(board.getBoardCategoryId())
//                .createDate(board.getCreateDate())
//                .build();
//    }
//
//    public static Board mapToEntity(BoardListDto boardListDto) {
//        if(boardListDto == null)
//            return null;
//
//        return Board.builder()
//                .id(boardListDto.getId())
//                .title(boardListDto.getTitle())
//                .content(boardListDto.getContent())
//                .
//
//
//
//
//    }
//}
