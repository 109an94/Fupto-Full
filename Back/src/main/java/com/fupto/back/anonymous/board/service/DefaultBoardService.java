package com.fupto.back.anonymous.board.service;

import com.fupto.back.admin.board.dto.BoardListDto;
import com.fupto.back.anonymous.board.dto.*;
import com.fupto.back.entity.Board;
import com.fupto.back.entity.BoardCategory;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.BoardCategoryRepository;
import com.fupto.back.repository.BoardRepository;
import com.fupto.back.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class DefaultBoardService implements BoardService {

    @Value("uploads")
    private String uploadPath;

    private BoardRepository boardRepository;
    private ModelMapper modelMapper;
    private BoardCategoryRepository boardCategoryRepository;
    private MemberRepository memberRepository;

    public DefaultBoardService(BoardRepository boardRepository,
                               ModelMapper modelMapper,
                               BoardCategoryRepository boardCategoryRepository,
                               MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.modelMapper = modelMapper;
        this.boardCategoryRepository = boardCategoryRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<BoardDto> findAll() {
        List<Board> boards = boardRepository.findAll();
        List<BoardDto> boardDtos = boards.stream()
                .filter(board -> board.getActive() != null && board.getActive())
                .map(board -> {
                    BoardDto boardDto = modelMapper.map(board, BoardDto.class);
                    return boardDto;
                })
                .toList();
        return boardDtos;
    }

    @Override
    public DefaultDto userSearch(SearchDto searchDto) {
        Sort sort = Sort.by(
                searchDto.getSortOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                searchDto.getSortBy() != null ? searchDto.getSortBy() : "createdAt"
        );
        Pageable pageable = PageRequest.of(searchDto.getPage() - 1, searchDto.getSize(), sort);

        Page<Board> boards = boardRepository.userSearchBoards(
                searchDto.getSearchKeyWord(),
                searchDto.getSearchType(),
                searchDto.getBoardCategory(),
                pageable
        );

        List<BoardDto> boardDtos = boards
                .getContent()
                .stream()
                .filter(board -> board.getActive() != null && board.getActive())
                .map(board -> {
                    BoardDto boardDto = modelMapper.map(board, BoardDto.class);
                    return boardDto;
                })
                .toList();

        long totalElements = boards.getTotalElements();
        long totalPages = boards.getTotalPages();

        List<Long> pages = new ArrayList<>();
        for (long i = 1; i <= totalPages; i++) {
            pages.add(i);
        }

        return DefaultDto
                .builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNextPage(boards.hasNext())
                .hasPreviousPage(boards.hasPrevious())
                .pages(pages)
                .boards(boardDtos)
                .build();
    }

    @Override
    public DetailDto getById(Long id) {
        Board board = boardRepository.findById(id).orElse(null);

        return DetailDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .boardCategoryName(board.getBoardCategory().getName())
                .regMemberId(board.getId())
                .regMemberNickName(board.getRegMember().getNickname())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .img(board.getImg())
                .build();
    }

    @Override
    public BoardDto userInActive(Long id, Boolean active) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        board.setActive(active);
        board.setModifiedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));

        boardRepository.save(board);

        return modelMapper.map(board, BoardDto.class);
    }

    @Override
    public BoardDto userPost(BoardDto boardDto, MultipartFile file) throws IOException {

        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setContents(boardDto.getContents());
        BoardCategory boardCategory = boardCategoryRepository.findById(boardDto.getBoardCategoryId())
                .orElseThrow(() -> new RuntimeException("BoardCategory not found"));
        board.setBoardCategory(boardCategory);
        board.setActive(boardDto.getActive());

        Member member = memberRepository.findById(boardDto.getRegMemberId())
                .orElseThrow(() -> new RuntimeException("RegMember not found"));
        board.setRegMember(member);

        Board savedBoard = boardRepository.save(board);

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, savedBoard.getId());  // 게시글 ID로 디렉토리 생성 후 파일 저장
            savedBoard.setImg(fileName);
        }

        savedBoard = boardRepository.save(savedBoard);

        BoardDto savedBoardDto = new BoardDto();
        savedBoardDto.setId(savedBoard.getId());
        savedBoardDto.setTitle(savedBoard.getTitle());
        savedBoardDto.setContents(savedBoard.getContents());
        savedBoardDto.setActive(savedBoard.getActive());
        savedBoardDto.setImg(savedBoard.getImg());
        savedBoardDto.setBoardCategoryId(savedBoard.getBoardCategory().getId());
        savedBoardDto.setRegMemberNickName(savedBoard.getRegMember().getNickname());
        savedBoardDto.setRegMemberId(savedBoard.getRegMember().getId());


        return savedBoardDto;
    }

    @Override
    public BoardDto show(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        return BoardDto.builder()
                .title(board.getTitle())
                .contents(board.getContents())
                .boardCategoryId(board.getBoardCategory().getId())

                .boardCategoryName(board.getBoardCategory().getName())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .img(board.getImg())
                .regMemberNickName(board.getRegMember().getNickname())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .img(board.getImg())
                .regMemberId(board.getRegMember().getId())
                .build();
    }

    @Override
    public BoardDto update(Long id, UserUpdateDto userUpdateDto, MultipartFile file) throws IOException {

        Board board = boardRepository.findById(id).orElse(null);
        board.setTitle(userUpdateDto.getTitle());
        board.setContents(userUpdateDto.getContents());
        board.setImg(userUpdateDto.getImg());

        BoardCategory boardCategory = boardCategoryRepository.findById(userUpdateDto.getBoardCategoryId())
                .orElseThrow(() -> new RuntimeException("BoardCategory not found"));
        board.setBoardCategory(boardCategory);

        Member member = memberRepository.findById(userUpdateDto.getRegMemberId())
                .orElseThrow(() -> new RuntimeException("BoardCategory not found"));
        board.setRegMember(member);

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, id);
            board.setImg(fileName);
        }

        board.setModifiedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toInstant(ZoneOffset.UTC));

        Board updatedBoard = boardRepository.save(board);

        return modelMapper.map(updatedBoard, BoardDto.class);
    }


    private String saveFile(MultipartFile file, Long id) throws IOException {
        // 파일 이름에 UUID 추가
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path boardUploadDir = Paths.get(uploadPath, "boards", id.toString());
        if (!Files.exists(boardUploadDir)) {
            Files.createDirectories(boardUploadDir);
        }

        // 최종 파일 경로 설정
        Path filePath = boardUploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 경로가 포함된 파일명을 반환 (백슬래시를 포워드 슬래시로 변경)
        return filePath.toString().replace("\\", "/");

    }

}
