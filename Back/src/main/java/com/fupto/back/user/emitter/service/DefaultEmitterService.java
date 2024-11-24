package com.fupto.back.user.emitter.service;

import com.fupto.back.entity.Alert;
import com.fupto.back.repository.AlertRepository;
import com.fupto.back.repository.MemberRepository;
import com.fupto.back.user.emitter.dto.AlertDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service("userEmitterService")
@Transactional
public class DefaultEmitterService implements EmitterService {
    private static final Long TIMEOUT = 5L * 60 * 1000;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Object>> eventCache = new ConcurrentHashMap<>();
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

        // 최초 연결시 캐시된 이벤트 전송
        Map<String, Object> cachedEvents = eventCache.getOrDefault(memberId, new ConcurrentHashMap<>());
        cachedEvents.forEach((eventId, event) -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name("alert")
                        .data(event));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        });

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    @Override
    public void sendToEmitter(Long memberId, String alertType, Object data) {
        // 이벤트 캐시에 저장
        String eventId = memberId + "_" + System.currentTimeMillis();
        eventCache.computeIfAbsent(memberId, k -> new ConcurrentHashMap<>())
                .put(eventId, data);

        // 실시간 전송
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name(alertType)
                        .data(data));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }

    // 캐시 정리 (주기적으로 호출)
    public void cleanEventCache(Long memberId) {
        eventCache.remove(memberId);
    }


    @Override
    public List<AlertDto> getUnreadAlerts(Long memberId) {
        return alertRepository.findByMemberIdAndIsReadFalseOrderByCreateDateDesc(memberId)
                .stream().map(alert -> AlertDto.builder()
                        .id(alert.getId())
                        .message(alert.getMessage())
                        .ceateDate(alert.getCreateDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void markAlertAsRead(Long memberId) {
        Alert alert = alertRepository.findByMemberId(memberId).orElseThrow( () ->
                new RuntimeException("Alert not found"));
        alert.setIsRead(true);
        alertRepository.save(alert);
    }

    @Override
    public void markAllAlertsAsRead(Long memberId) {
        List<Alert> alerts = alertRepository.findAllByMemberId(memberId);
        alerts.forEach(alert -> alert.setIsRead(true));
        alertRepository.saveAll(alerts);
    }

    @Override
    public void removeEmitter(Long memberId) {
        emitters.remove(memberId);
    }
}