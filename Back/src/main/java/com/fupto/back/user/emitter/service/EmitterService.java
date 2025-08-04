package com.fupto.back.user.emitter.service;

import com.fupto.back.entity.Alert;
import com.fupto.back.user.emitter.dto.AlertDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface EmitterService {
    SseEmitter createEmitter(Long memberId);
    void removeEmitter(Long memberId);
    void sendToEmitter(Long memberId, String alertType, Object data);
    List<AlertDto> getUnreadAlerts(Long memberId);
    void markAlertAsRead(Long alertId);
    void markAllAlertsAsRead(Long alertId);

}
