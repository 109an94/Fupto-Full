package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.entity.Board;
import com.fupto.back.entity.BoardCategory;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.BoardCategoryRepository;
import com.fupto.back.repository.BoardRepository;
import com.fupto.back.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class DefaultBoardService implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;

    public DefaultBoardService(BoardRepository boardRepository,
                               BoardCategoryRepository boardCategoryRepository,
                               ModelMapper modelMapper,
                               MemberRepository memberRepository) {

        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
        this.boardCategoryRepository = boardCategoryRepository;
        this.memberRepository = memberRepository;
    }

//    public DefaultBoardService(BoardRepository boardRepository, ModelMapper modelMapper) {
//        this.boardRepository = boardRepository;
//        this.modelMapper = modelMapper;
//    }

// ========== 전체 조회 =========================================================================
    @Override
    public List<BoardListDto> getList() {
        List<Board> boards = boardRepository.findAll();
        List<BoardListDto> boardListDtos = boards.stream()
                .map(board -> {BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;})
                .toList();
        return boardListDtos;
    }

// ========== 등록 =========================================================================
    @Override
    public BoardListDto createPost(BoardListDto boardListDto) {
        // BoardListDto -> Board Entity 변환
        Board newBoard = modelMapper.map(boardListDto, Board.class);

        // 카테고리
        Long boardCategoryId = boardListDto.getBoardCategoryId();  // BoardListDto에서 category ID를 가져옴
        BoardCategory boardCategory = boardCategoryRepository.findById(boardCategoryId)
                .orElseThrow(() -> new RuntimeException("BoardCategory not found"));

        newBoard.setBoardCategory(boardCategory);

        // 멤버

        Long regMemberId = boardListDto.getRegMemberId();
        Member member = memberRepository.findById(regMemberId).orElseThrow(() -> new RuntimeException("RegMember not found"));

        newBoard.setRegMember(member);
        newBoard.setCreatedAt(Instant.now());


        // DB에 게시글 저장
        Board savedBoard = boardRepository.save(newBoard);

        // 저장된 게시글을 BoardListDto로 변환하여 반환
        return modelMapper.map(savedBoard, BoardListDto.class);
    }


// ========== id 조회 =========================================================================
    @Override
    public BoardDetailDto getBoardById(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board == null) {
            return null;
        }
        BoardDetailDto boardDetailDto = modelMapper.map(board, BoardDetailDto.class);

        return boardDetailDto;
    }

// ========== 수정 =========================================================================
    @Override
    public BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
        if (!requestsDto.getPassword().equals(board.getPassword()))
            throw new Exception("비밀번호가 일치하지 않습니다.");

        board.update(requestsDto);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }


// ========== 삭제 =========================================================================
    @Override
    public SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        if (!requestsDto.getPassword().equals(board.getPassword()))
            throw new Exception("비밀번호가 일치하지 않습니다.");

        boardRepository.deleteById(id);
        return new SuccessResponseDto(true);
    }

}


