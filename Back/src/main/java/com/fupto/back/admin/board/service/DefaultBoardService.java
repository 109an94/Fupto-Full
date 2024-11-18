package com.fupto.back.admin.board.service;

import com.fupto.back.admin.board.dto.*;
import com.fupto.back.entity.Board;
import com.fupto.back.entity.BoardCategory;
import com.fupto.back.entity.Brand;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.BoardCategoryRepository;
import com.fupto.back.repository.BoardRepository;
import com.fupto.back.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // ========== 전체 조회 =========================================================================
    @Override
    public List<BoardListDto> getList() {
        List<Board> boards = boardRepository.findAll();
        List<BoardListDto> boardListDtos = boards.stream()
                .map(board -> {
                    BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;
                })
                .toList();
        return boardListDtos;
    }

    // ========== 검색 조회 =========================================================================
    @Override
    public BoardDefaultDto getSearch(BoardSearchDto boardSearchDto) {
        Sort sort = Sort.by(
                boardSearchDto.getSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                boardSearchDto.getSortBy() != null ? boardSearchDto.getSortBy() : "createDate"
        );
        Pageable pageable = PageRequest.of(boardSearchDto.getPage() - 1, boardSearchDto.getSize(), sort);

        Instant startDate = null;
        Instant endDate = null;

        try {
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

            if (boardSearchDto.getStartDate() != null && !boardSearchDto.getStartDate().isEmpty()) {
                LocalDate localStratDate = LocalDate.parse(boardSearchDto.getStartDate(), DateTimeFormatter.ISO_DATE);
                startDate = localStratDate.atStartOfDay(zoneId).plusHours(9).toInstant();
            }
            if (boardSearchDto.getEndDate() != null && !boardSearchDto.getEndDate().isEmpty()) {
                LocalDate localEndDate = LocalDate.parse(boardSearchDto.getEndDate(), DateTimeFormatter.ISO_DATE);
                // 종료일의 23:59:59를 한국 표준시로 설정 후 9시간 추가
                endDate = localEndDate.atTime(LocalTime.of(23, 59, 59)).atZone(zoneId).plusHours(9).toInstant();
                System.out.println(formatter.format(endDate));
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd format.", e);
        }

        Page<Board> boardPage = boardRepository.searchBoards(
                boardSearchDto.getSearchKeyWord(),
                boardSearchDto.getSearchType(),
                boardSearchDto.getActive(),
                boardSearchDto.getBoardCategory(),
                startDate,
                endDate,
                boardSearchDto.getDateType(),
                pageable
        );

        List<BoardListDto> boardListDtos = boardPage
                .getContent()
                .stream()
                .map(board -> {
                    BoardListDto boardListDto = modelMapper.map(board, BoardListDto.class);
                    return boardListDto;
                })
                .toList();

        long totalElements = boardPage.getTotalElements();
        long totalPages = boardPage.getTotalPages();

        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        return BoardDefaultDto
                .builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNextPage(boardPage.hasNext())
                .hasPreviousPage(boardPage.hasPrevious())
                .pages(pages)
                .boards(boardListDtos)
                .build();
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

// ========== 등록 =========================================================================
//@Override
//public BoardListDto createPost(@RequestParam("title") String title,
//                               @RequestParam("contents") String contents,
//                               @RequestParam("boardCategoryName") String boardCategoryName,
//                               @RequestParam("file") MultipartFile file,
//                               @RequestParam("boardCategoryId") Long boardCategoryId,
//                               @RequestParam("regMemberId") Long regMemberId) {
//
//    }

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

    // ========== 수정 =========================================================================
    @Override
    public BoardResponseDto updatePost(Long id, BoardRequestsDto requestsDto) throws Exception {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
//        if (!requestsDto.getPassword().equals(board.getPassword()))
//            throw new Exception("비밀번호가 일치하지 않습니다.");

        board.update(requestsDto);
        boardRepository.save(board);

        return new BoardResponseDto(board);
    }


    // ========== 삭제 =========================================================================
//    @Override
//    public SuccessResponseDto deletePost(Long id, BoardRequestsDto requestsDto) throws Exception {
//        Board board = boardRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
//        );
////        if (!requestsDto.getPassword().equals(board.getPassword()))
////            throw new Exception("비밀번호가 일치하지 않습니다.");
//
//        boardRepository.deleteById(id);
//        return new SuccessResponseDto(true);
//    }
    @Override
    public SuccessResponseDto deletePost(Long id) throws Exception {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
//        if (!requestsDto.getPassword().equals(board.getPassword()))
//            throw new Exception("비밀번호가 일치하지 않습니다.");

        boardRepository.deleteById(id);
        return new SuccessResponseDto(true);
    }

    @Override
    public void deleteSelected(List<Long> ids) {
        List<Board> boards = boardRepository.findAllById(ids);
        for (Board board : boards) {
            boardRepository.delete(board);
        }
    }

// ========== 엑티브 =========================================================================

    @Override
    public BoardListDto updateActive(Long id, Boolean active) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.setActive(active);
//        board.setModifiedAt(Instant.now());
        board.setModifiedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));


        boardRepository.save(board);

        return modelMapper.map(board, BoardListDto.class);
    }


}

