package com.fupto.back.user.emitter.service;

import com.fupto.back.entity.Alert;
import com.fupto.back.repository.AlertRepository;
import com.fupto.back.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service("userEmiterService")
@Transactional
public class DefaultEmitterService implements EmitterService {
    private static final Long TIMEOUT = 5L * 60 * 1000;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;

    public DefaultEmitterService(AlertRepository alertRepository,
                                 MemberRepository memberRepository) {
        this.alertRepository = alertRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    @Override
    public void sendToEmitter(Long memberId, String alertType, Object data) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(alertType).data(data));
                //알림 찾기
                Optional<Alert> existingAlert = alertRepository.findByMemberIdAndAlertType(memberId,alertType);
                Alert alert;
                if (existingAlert.isPresent()) {
                    //기존 알림 덮어쓰기
                    alert = existingAlert.get();
                    alert.setMessage(data.toString());
                    alert.setCreateDate(Instant.now());
//                    alert.setReferenceId();
                    alert.setIsRead(false);
                    alertRepository.save(alert);
                } else {
                    // 새 알림 저장
                    alert = new Alert();
                    alert.setMember(memberRepository.findById(memberId).get());
                    alert.setAlertType(alertType);
                    alert.setMessage(data.toString());
                    alert.setCreateDate(Instant.now());
//                    alert.setReferenceId();
                    alert.setIsRead(false);
                    alertRepository.save(alert);
                }

            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }

    @Override
    public void removeEmitter(Long memberId) {
        emitters.remove(memberId);
    }
}