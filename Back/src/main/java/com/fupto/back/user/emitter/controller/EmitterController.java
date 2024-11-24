package com.fupto.back.user.emitter.controller;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.user.emitter.dto.AlertEventDto;
import com.fupto.back.user.emitter.service.EmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController("userEmitterController")
@RequestMapping("/user/member")
public class EmitterController {
    private final EmitterService emitterService;

    @Autowired
    public EmitterController(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal FuptoUserDetails userDetails) {
        return emitterService.createEmitter(userDetails.getId());
    }

    // 테스트용 엔드포인트 (실제 운영에서는 제거하세요)
    @PostMapping("/test-alert")
    public ResponseEntity<?> sendTestAlert(@AuthenticationPrincipal FuptoUserDetails userDetails,
                                           @RequestBody AlertEventDto alertEventDto) {
        emitterService.sendToEmitter(userDetails.getId(), "alert", alertEventDto);
        return ResponseEntity.ok().build();
    }
}
