package com.fupto.back.user.emitter.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterService {
    SseEmitter createEmitter(Long memberId);
    void removeEmitter(Long memberId);
    void sendToEmitter(Long memberId, String eventName, Object data);
}
