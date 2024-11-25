package com.fupto.back.user.member.controller;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.user.emitter.dto.AlertPriceDto;
import com.fupto.back.user.member.dto.BoardListDto;
import com.fupto.back.user.member.dto.MemberEditDto;
import com.fupto.back.user.member.dto.MemberResponseDto;
import com.fupto.back.user.member.exception.InvalidPasswordException;
import com.fupto.back.user.member.service.MemberService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController("userMemberController")
@RequestMapping("/user/member")
public class MemberController {
    private MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PutMapping("edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MemberResponseDto> signUp(@AuthenticationPrincipal FuptoUserDetails userDetails,
                                                    @RequestBody MemberEditDto requestDto) {
        try {
            MemberResponseDto editMember = memberService.editMember(userDetails.getUsername(), requestDto);
            System.out.println(editMember);
            return ResponseEntity.ok(editMember);
        }catch (InvalidPasswordException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id){
        System.out.println("getmember 실행 확인");
        return ResponseEntity.ok(memberService.getMember(id));
    }
//  board 영역 ---------------------
    @GetMapping("{id}/boards")
    public ResponseEntity<List<BoardListDto>> getBoards(@PathVariable Long id){

        return ResponseEntity.ok(memberService.getBoards(id));
    }
    @GetMapping("{id}/boardimg")//이미지 ID값
    public ResponseEntity<Resource> getBordsImg(@PathVariable Long id) throws IOException {
        Resource resource = memberService.getBoardImage(id);
        String contentType = Files.probeContentType(Paths.get(resource.getURI()));

        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        System.out.println("---------------컨트롤러 리턴직전------------");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

//    Favorite 영역-------------------------
    @GetMapping("{id}/fav")//계정 ID값
    public ResponseEntity<?> getFavorites(@PathVariable Long id){
        System.out.println(id);
        return ResponseEntity.ok(memberService.getFavorites(id));
    }
    @GetMapping("{id}/favimg")//이미지 ID값
    public ResponseEntity<Resource> getMembersFavImg(@PathVariable Long id) throws IOException {
        Resource resource = memberService.getProductImage(id);
        String contentType = Files.probeContentType(Paths.get(resource.getURI()));

        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        System.out.println("---------------컨트롤러 리턴직전------------");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


        @PostMapping("{member}/fav/{mappingId}/alertPrice")
    public ResponseEntity<AlertPriceDto> updateAlertPrice(
//            @AuthenticationPrincipal FuptoUserDetails userDetails,
                                                          @PathVariable Long member,
                                                          @PathVariable Long mappingId,
                                                          @RequestBody AlertPriceDto alertPriceDto){
//        Long memberId = userDetails.getId();
        memberService.updateAlertPrice(member, mappingId, alertPriceDto.getAlertPrice());
        return ResponseEntity.ok().build();
    }

}
