package com.fupto.back.user.member.controller;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.user.emitter.dto.AlertPriceDto;
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



//    Favorite 영역-------------------------
    @GetMapping("{id}/fav")
    public ResponseEntity<?> getFavorites(@PathVariable Long id){
        System.out.println(id);
        return ResponseEntity.ok(memberService.getFavorites(id));
    }
    @GetMapping("{id}/favimg")
    public ResponseEntity<Resource> getMembersFavImg(@PathVariable Long id) throws IOException {
        Resource resource = memberService.getProductImage(id);
        String contentType = Files.probeContentType(Paths.get(resource.getURI()));

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


        @PostMapping("{id}/fav/{favoriteId}/alertPrice")
    public ResponseEntity<AlertPriceDto> updateAlertPrice(
//            @AuthenticationPrincipal FuptoUserDetails userDetails,
                                                          @PathVariable Long id,
                                                          @PathVariable Long favoriteId,
                                                          @RequestBody AlertPriceDto alertPriceDto){
//        Long memberId = userDetails.getId();
        Long memberId = id;
        memberService.updateAlertPrice(favoriteId, memberId, alertPriceDto.getAlertPrice());
        return ResponseEntity.ok().build();
    }

}
