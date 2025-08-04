package com.fupto.back.user.emitter.controller;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.user.emitter.dto.AlertDto;
import com.fupto.back.user.emitter.dto.AlertEventDto;
import com.fupto.back.user.emitter.service.EmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController("userEmitterController")
@RequestMapping("/user/member")
public class EmitterController {
    private final EmitterService emitterService;
    public EmitterController(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal FuptoUserDetails userDetails) {
        System.out.println("-------------------subscribe----------------");
        
        SseEmitter emitter = emitterService.createEmitter(userDetails.getId());

        try {
            // 초기 연결 확인을 위한 더미 데이터 전송
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }


        return emitter;
    }

    @GetMapping("/unreadAlerts")
    public ResponseEntity<List<AlertDto>> getUnreadAlerts(@AuthenticationPrincipal FuptoUserDetails userDetails) {
        return ResponseEntity.ok(emitterService.getUnreadAlerts(userDetails.getId()));
    }

    @PatchMapping("/alerts/{alertId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long alertId) {
        emitterService.markAlertAsRead(alertId);
        System.out.println("-------작동확인-------"+alertId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/readAll")
    public ResponseEntity<?> markAsReadAll(@AuthenticationPrincipal FuptoUserDetails userDetails) {
        emitterService.markAllAlertsAsRead(userDetails.getId());
        return ResponseEntity.ok().build();
    }



    // 테스트용 엔드포인트 (실제 운영에서는 제거하세요)
    @PostMapping("/test-alert")
    public ResponseEntity<?> sendTestAlert(@AuthenticationPrincipal FuptoUserDetails userDetails,
                                           @RequestBody AlertEventDto alertEventDto) {
        emitterService.sendToEmitter(userDetails.getId(), "alert", alertEventDto);
        return ResponseEntity.ok().build();
    }
}
